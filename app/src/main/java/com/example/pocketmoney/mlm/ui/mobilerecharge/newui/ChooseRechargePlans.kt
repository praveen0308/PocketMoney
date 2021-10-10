package com.example.pocketmoney.mlm.ui.mobilerecharge.newui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentChooseRechargePlansBinding
import com.example.pocketmoney.mlm.adapters.OperatorPlanPagerAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.Records
import com.example.pocketmoney.mlm.model.serviceModels.SimplePlanResponse
import com.example.pocketmoney.mlm.ui.mobilerecharge.MobileOperatorPlanPagerFragment
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PlanType
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseRechargePlans(val planType:PlanType = PlanType.NORMAL_PLAN) : BaseBottomSheetDialogFragment<FragmentChooseRechargePlansBinding>(FragmentChooseRechargePlansBinding::inflate),
    MobileOperatorPlanPagerFragment.MobileOperatorPlanPagerInterface {

    // ViewModel
    private val viewModel by activityViewModels<MobileRechargeViewModel>()

    //Variables
    private var numberOfTab: Int? = -1
    private var specialPlanList = mutableListOf<MobileOperatorPlan>()

    private lateinit var mContact: ModelContact

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        if (planType == PlanType.SPECIAL_PLAN){
            viewModel.getMobileSpecialPlanList(viewModel.rechargeMobileNo.value!!, viewModel.selectedOperator.value!!)
        }else{


            viewModel.getMobileSimplePlanList(viewModel.selectedCircle.value!!, viewModel.selectedOperator.value!!)
        }*/
    }

    override fun subscribeObservers() {
        /*viewModel.selectedContact.observe(viewLifecycleOwner, {
            mContact = it



        })*/


        viewModel.mobileSpecialPlanList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        specialPlanList.clear()
                        specialPlanList.addAll(it)
                        val records = Records(specialPlanList = specialPlanList)
                        setupTLWithViewPager(getOperatorPlanList(records))

                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })


        viewModel.mobileSimplePlanList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

//                        it.records.specialPlanList = specialPlanList
                        if (planType != PlanType.SPECIAL_PLAN){
                            setupTLWithViewPager(getOperatorPlanList(it.records))
                        }


                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

    }


    private fun setupTLWithViewPager(operatorPlanList: List<ModelOperatorPlan>) {
        binding.tabLayout.removeAllTabs()
//        binding.viewPager.removeAllViews()
        numberOfTab = operatorPlanList.size - 1
        for (i in 0..numberOfTab!!) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(operatorPlanList[i].title))
        }
        val pagerViewAdapter = activity?.let {
            OperatorPlanPagerAdapter(
                binding.tabLayout.tabCount,
                operatorPlanList,
                it.supportFragmentManager,
                lifecycle,
                this
            )
        }
        binding.viewPager.adapter = pagerViewAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = operatorPlanList[position].title
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun getOperatorPlanList(records: Records): List<ModelOperatorPlan> {
        val operatorPlanList = mutableListOf<ModelOperatorPlan>()

        if (!records.Plan2G.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "2G",
                records.Plan2G
            )
        )
        if (!records.Plan3G_4G.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "3G/4G",
                records.Plan3G_4G
            )
        )
        if (!records.TOP_UP.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "Topup",
                records.TOP_UP
            )
        )
        if (!records.specialPlanList.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "Special Offer",
                records.specialPlanList
            )
        )
        if (!records.SMS.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "SMS",
                records.SMS
            )
        )
        if (!records.COMBO.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "Combo",
                records.COMBO
            )
        )
        if (!records.RATE_CUTTER.isNullOrEmpty()) operatorPlanList.add(
            ModelOperatorPlan(
                "Rate Cutter",
                records.RATE_CUTTER
            )
        )

        return operatorPlanList

    }

    override fun onPlanChosen(plan: MobileOperatorPlan) {
        viewModel.rechargeAmount.postValue(Integer.parseInt(plan.rs))
        viewModel.setSelectedMobileOperatorPlan(plan)
        dismiss()
    }

}