package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSelectRechargePlanBinding
import com.example.pocketmoney.mlm.adapters.OperatorPlanPagerAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.MobileCircleOperator
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.Records

import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRechargePlan :
    BaseFragment<FragmentSelectRechargePlanBinding>(FragmentSelectRechargePlanBinding::inflate),
    MyCustomToolbar.MyCustomToolbarListener,
    MobileOperatorPlanPagerFragment.MobileOperatorPlanPagerInterface {

    // UI

    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val viewModel by activityViewModels<MobileRechargeViewModel>()

    // Interface


    //Variables
    private val args by navArgs<SelectRechargePlanArgs>()
    private var numberOfTab: Int? = -1
    private var specialPlanList = mutableListOf<MobileOperatorPlan>()
    private var mCircle: String = "Mumbai"
    private var mOperator: String = "Jio"
    private lateinit var mContact: ModelContact

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK){
                    val operator = data!!.getStringExtra("operator")!!
                    val circle = data.getStringExtra("circle")!!
                    updateOperatorNCircle(operator,circle)

                    viewModel.getMobileSpecialPlanList(mContact.contactNumber!!, mOperator)

                }
                else{
                    showToast("Cancelled !!")
                }

            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSelectRechargePlan.setCustomToolbarListener(this)
//        val mobileNumber = args.mobileNumber
//        viewModel.getCircleNOperatorOfMobileNo(mobileNumber)
        binding.apply {
            btnChangeOperator.setOnClickListener {
                val intent = Intent(requireActivity(),MobileNumberDetail::class.java)
                intent.putExtra("operator",mOperator)
                intent.putExtra("circle",mCircle)
                resultLauncher.launch(intent)
            }

        }
    }

    private fun updateOperatorNCircle(operator:String,circle:String){
        mOperator = operator
        mCircle = circle
        mContact.circle = mCircle
        mContact.operator = mOperator
        populateUiElements()
    }

    override fun subscribeObservers() {

        viewModel.selectedContact.observe(viewLifecycleOwner, {
            mContact = it

            viewModel.getCircleNOperatorOfMobileNo(mContact.contactNumber!!)
        })
        viewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        it.circle?.let { circle ->
                            mCircle = circle

                        }
                        it.Operator?.let { operator ->
                            mOperator = operator
                        }
                        mContact.circle = mCircle
                        mContact.operator = mOperator

                        viewModel.getMobileSpecialPlanList(args.mobileNumber, mOperator)
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


        viewModel.mobileSpecialPlanList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        specialPlanList.clear()
                        specialPlanList.addAll(it)
                        viewModel.getMobileSimplePlanList(mCircle, mOperator)
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


    private fun populateUiElements() {
        binding.toolbarSelectRechargePlan.apply {
            setToolbarLogo(getMobileOperatorLogo(mContact.operator!!))
            if (mContact.contactName.isNullOrEmpty()) {
                setToolbarTitle(mContact.contactNumber!!)
                setToolbarSubtitle("Prepaid - ${mContact.circle}")

            } else {
                setToolbarTitle(mContact.contactName!!)
                setToolbarSubtitle("${mContact.contactNumber} - ${mContact.circle}")
            }
        }
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

//    override fun onPlanChosen(plan: MobileOperatorPlan) {
//
//    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {
        val intent = Intent(requireActivity(),MobileNumberDetail::class.java)
        intent.putExtra("operator",mOperator)
        intent.putExtra("circle",mCircle)
        resultLauncher.launch(intent)
    }

    override fun onPlanChosen(plan: MobileOperatorPlan) {
        viewModel.setSelectedMobileOperatorPlan(plan)
//        findNavController().navigate(R.id.action_selectRechargePlan_to_confirmRecharge,ConfirmRechargeArgs(args.mobileNumber,args.contactName).toBundle())
        findNavController().navigate(R.id.action_selectRechargePlan_to_confirmRecharge)
    }
}