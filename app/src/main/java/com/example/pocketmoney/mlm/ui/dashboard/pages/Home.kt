package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentHomeBinding
import com.example.pocketmoney.mlm.HomeParentItemListener
import com.example.pocketmoney.mlm.adapters.HomeParentAdapter
import com.example.pocketmoney.mlm.model.*
import com.example.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.mlm.viewmodel.WalletViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.ProgressBarHandler

import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.MyEnums
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Home : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), HomeParentItemListener {

    // UI
    private lateinit var navController: NavController

    // Adapter

    // ViewModel
    private val walletViewModel: WalletViewModel by viewModels()
    private val accountViewModel : AccountViewModel by viewModels()

    // Interface
    private lateinit var fragmentListener : HomeFragmentListener

    //Variables
    private var walletDetailVisibility: Boolean = false
    private var userID : String=""
    private var roleID : Int=0

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        fragmentListener = activity as MainDashboard
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.bottomLayout.mainDashboardParentRecyclerView.setHasFixedSize(true)
        binding.bottomLayout.mainDashboardParentRecyclerView.layoutManager =
            LinearLayoutManager(context)
        binding.bottomLayout.mainDashboardParentRecyclerView.adapter =
            HomeParentAdapter(prepareHomeData(),this,requireActivity())


        binding.topLayout.layoutWalletBalanceView.setOnClickListener {
            walletDetailVisibility = if (walletDetailVisibility) {
                binding.root.transitionToStart()
                false
            } else {
                binding.root.transitionToState(R.id.end2)
                true
            }
        }

        binding.walletDetailView.cdClose.setOnClickListener {
            binding.root.transitionToStart()
            walletDetailVisibility=false
        }

        binding.topLayout.ivUserProfile.setOnClickListener{
         fragmentListener.onUserProfileClick()
        }

    }
    override fun subscribeObservers() {
        accountViewModel.userID.observe(viewLifecycleOwner, {
            userID = it

        })
        accountViewModel.roleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userID!="" && roleID!=0){
                walletViewModel.getWalletBalance(userID,roleID)
                walletViewModel.getPCashBalance(userID,roleID)
            }

        })
        walletViewModel.walletBalance.observe(viewLifecycleOwner, Observer { _result ->
            when(_result.status)
            {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.walletDetailView.tvWalletBalance.text = "₹".plus(it.toString())
                        binding.topLayout.walletBalanceView.tvWalletBalance.text = "₹".plus(it.toString())
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
        walletViewModel.pCash.observe(viewLifecycleOwner, { _result ->
            when(_result.status)
            {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.walletDetailView.tvPCash.text = "₹".plus(it.toString())
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


    private fun prepareBannerList(): ArrayList<ModelBanner> {
        val bannerList: ArrayList<ModelBanner> = ArrayList()

        bannerList.add(
            ModelBanner(
                "Send money from credit card to bank",
                R.drawable.img_pay_bills,
                R.color.colorPrimaryLight
            )
        )
        bannerList.add(
            ModelBanner(
                "Send money from credit card to bank",
                R.drawable.img_pay_bills,
                R.color.colorPrimaryLight
            )
        )
        bannerList.add(
            ModelBanner(
                "Send money from credit card to bank",
                R.drawable.img_pay_bills,
                R.color.colorPrimaryLight
            )
        )
        bannerList.add(
            ModelBanner(
                "Send money from credit card to bank",
                R.drawable.img_pay_bills,
                R.color.colorPrimaryLight
            )
        )
        bannerList.add(
            ModelBanner(
                "Send money from credit card to bank",
                R.drawable.img_pay_bills,
                R.color.colorPrimaryLight
            )
        )

        return bannerList
    }

    private fun prepareHomeData(): List<HomeParentModel> {

        val dataList: MutableList<HomeParentModel> = ArrayList()


        val services1: MutableList<ModelServiceView> = java.util.ArrayList()
        services1.add(ModelServiceView("Add Money", R.drawable.ic_add_money, RechargeEnum.ADD_MONEY))
        services1.add(ModelServiceView("History", R.drawable.ic_history, RechargeEnum.PAYMENT_HISTORY))
        services1.add(ModelServiceView("Wallet", R.drawable.ic_wallet, RechargeEnum.WALLET))
        services1.add(ModelServiceView("Online Shopping", R.drawable.ic_shopping, RechargeEnum.SHOPPING))


        val servicesCategory1 = ModelServiceCategory("My Pocket", services1)
        val model1 = HomeParentModel(
            MyEnums.SERVICES,
            servicesCategory1
        )

        val services2: MutableList<ModelServiceView> = java.util.ArrayList()
        services2.add(ModelServiceView("Prepaid", R.drawable.ic_prepaid, RechargeEnum.PREPAID))
        services2.add(ModelServiceView("Postpaid", R.drawable.ic_postpaid, RechargeEnum.POSTPAID))
        services2.add(ModelServiceView("DTH", R.drawable.ic_dth, RechargeEnum.DTH))
        services2.add(ModelServiceView("Landline", R.drawable.ic_landline, RechargeEnum.LANDLINE))
        services2.add(ModelServiceView("Electricity", R.drawable.ic_electricity, RechargeEnum.ELECTRICITY))
        services2.add(ModelServiceView("Water", R.drawable.ic_water, RechargeEnum.WATER))
        services2.add(ModelServiceView("Gas Cylinder Booking", R.drawable.ic_gas, RechargeEnum.GAS))
        services2.add(ModelServiceView("Broadband", R.drawable.ic_broadband, RechargeEnum.BROADBAND))
        services2.add(ModelServiceView("Loans", R.drawable.ic_loan, RechargeEnum.LOAN))
        services2.add(ModelServiceView("DMT", R.drawable.ic_dmt, RechargeEnum.DMT))
        services2.add(
            ModelServiceView(
                "Life Insurance",
                R.drawable.ic_life_insurance,
                RechargeEnum.LIFE_INSURANCE
            )
        )
        services2.add(ModelServiceView("FASTag", R.drawable.ic_fastag, RechargeEnum.FASTAG))

        val servicesCategory2 = ModelServiceCategory(
            "Featured", services2
        )

        val model2 = HomeParentModel(MyEnums.SERVICES, servicesCategory2)


        val bannerList: MutableList<ModelBanner> = java.util.ArrayList()
        bannerList.add(ModelBanner("First", R.drawable.banner1, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner2, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner3, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner4, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner5, 1))

        val model3 = HomeParentModel(MyEnums.OFFERS, offerBannerList = bannerList)

        dataList.add(model1)
        dataList.add(model3)
        dataList.add(model2)
        return dataList
    }

    override fun onItemClick(viewType: MyEnums, action: RechargeEnum) {
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show()
        when(viewType){
            MyEnums.SERVICES -> performActionForServices(action)
        }
    }

    private fun performActionForServices(action:RechargeEnum){
        when(action){
            RechargeEnum.PREPAID,RechargeEnum.POSTPAID -> navController.navigate(R.id.action_home_to_mobileRechargeActivity)
            RechargeEnum.DTH->navController.navigate(R.id.action_home_to_dthActivity)
            RechargeEnum.ELECTRICITY->navController.navigate(R.id.action_home_to_electricityActivity)
            RechargeEnum.SHOPPING -> navController.navigate(R.id.action_home_to_shop)
            RechargeEnum.PAYMENT_HISTORY -> navController.navigate(R.id.action_home_to_paymentHistory)
            else -> navController.navigate(HomeDirections.actionHomeToRechargeActivity(action))
        }
    }

    interface HomeFragmentListener{
        fun onUserProfileClick()
    }

}