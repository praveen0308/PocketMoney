package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentHomeBinding
import com.example.pocketmoney.databinding.FragmentSelectContactBinding
import com.example.pocketmoney.databinding.FragmentSelectRechargePlanBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.adapters.OperatorPlanPagerAdapter
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.mlm.model.ModelPlan
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.Records
import com.example.pocketmoney.mlm.ui.dashboard.pages.Home
import com.example.pocketmoney.mlm.ui.recharge.MobileOperatorPlanPagerFragment
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.mlm.viewmodel.WalletViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.example.pocketmoney.utils.myEnums.PaymentHistoryFilterEnum
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRechargePlan : BaseFragment<FragmentSelectRechargePlanBinding>(FragmentSelectRechargePlanBinding::inflate),
    MobileOperatorPlanPagerFragment.MobileOperatorPlanPagerInterface, MyCustomToolbar.MyCustomToolbarListener {

    // UI

    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val rechargeViewModel by activityViewModels<RechargeViewModel>()

    // Interface


    //Variables
    private val args by navArgs<SelectRechargePlanArgs>()
    private var numberOfTab: Int? = -1
    private var specialPlanList = mutableListOf<MobileOperatorPlan>()
    private lateinit var circle : String
    private lateinit var operator : String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSelectRechargePlan.setCustomToolbarListener(this)
        val mobileNumber = args.mobileNumber
        rechargeViewModel.getCircleNOperatorOfMobileNo(mobileNumber)

    }


    override fun subscribeObservers() {

        rechargeViewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        circle = it.circle
                        operator = it.Operator
                        rechargeViewModel.getMobileSpecialPlanList(args.mobileNumber,operator)
                        populateUiElements()
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


        rechargeViewModel.mobileSpecialPlanList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        specialPlanList.clear()
                        specialPlanList.addAll(it)
                        rechargeViewModel.getMobileSimplePlanList(circle,operator)
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


        rechargeViewModel.mobileSimplePlanList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        it.records.specialPlanList = specialPlanList
                        setupTLWithViewPager(getOperatorPlanList(it.records))
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

    private fun populateUiElements(){
        binding.toolbarSelectRechargePlan.apply {
            setToolbarLogo(getMobileOperatorLogo(operator))
            if (args.contactName.isNullOrEmpty()){
                setToolbarTitle(args.mobileNumber)
            }else setToolbarTitle(args.contactName)
            setToolbarSubtitle("${args.mobileNumber} - $circle")
        }
    }

    private fun setupTLWithViewPager(operatorPlanList: List<ModelOperatorPlan>){
        binding.tabLayout.removeAllTabs()
//        binding.viewPager.removeAllViews()
        numberOfTab = operatorPlanList.size-1
        for (i in 0..numberOfTab!!){
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(operatorPlanList[i].title))
        }
        val pagerViewAdapter = activity?.let { OperatorPlanPagerAdapter(binding.tabLayout.tabCount, operatorPlanList, it.supportFragmentManager, lifecycle,this) }
        binding.viewPager.adapter = pagerViewAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = operatorPlanList[position].title
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun getOperatorPlanList(records: Records):List<ModelOperatorPlan>{
        val operatorPlanList = mutableListOf<ModelOperatorPlan>()

        if (!records.Plan2G.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("2G",records.Plan2G))
        if (!records.Plan3G_4G.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("3G/4G",records.Plan3G_4G))
        if (!records.TOP_UP.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("Topup",records.TOP_UP))
        if (!records.specialPlanList.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("Special Offer",records.specialPlanList))
        if (!records.SMS.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("SMS",records.SMS))
        if (!records.COMBO.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("Combo",records.COMBO))
        if (!records.RATE_CUTTER.isNullOrEmpty()) operatorPlanList.add(ModelOperatorPlan("Rate Cutter",records.RATE_CUTTER))

        return operatorPlanList

    }

    override fun onPlanChosen(plan: MobileOperatorPlan) {
        rechargeViewModel.setSelectedMobileOperatorPlan(plan)
        findNavController().navigate(R.id.action_selectRechargePlan_to_confirmRecharge,ConfirmRechargeArgs(args.mobileNumber,args.contactName).toBundle())
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }
}