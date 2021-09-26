package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.app.Activity
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentHomeBinding
import com.example.pocketmoney.mlm.HomeParentItemListener
import com.example.pocketmoney.mlm.adapters.HomeParentAdapter
import com.example.pocketmoney.mlm.model.*
import com.example.pocketmoney.mlm.ui.AddMoneyToWallet
import com.example.pocketmoney.mlm.ui.dashboard.CustomerWalletActivity
import com.example.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.example.pocketmoney.mlm.ui.membership.MembershipPlanDetails
import com.example.pocketmoney.mlm.ui.membership.UpgradeToPro
import com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui.NewRechargeActivity
import com.example.pocketmoney.mlm.ui.payouts.NewPayout
import com.example.pocketmoney.mlm.ui.payouts.Payouts
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.mlm.viewmodel.HomeViewModel
import com.example.pocketmoney.mlm.viewmodel.WalletViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.ProgressBarHandler

import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.MyEnums
import com.example.pocketmoney.utils.setAmount
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Home : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    HomeParentItemListener {

    // ViewModel
    private val viewModel by viewModels<HomeViewModel>()

    // Interface
    private lateinit var fragmentListener: HomeFragmentListener

    //Variables
    private var walletDetailVisibility: Boolean = false
    private var userID: String = ""
    private var roleID: Int = 0

    private var isAccountActive = false
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        fragmentListener = activity as MainDashboard
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomLayout.mainDashboardParentRecyclerView.setHasFixedSize(true)
        binding.bottomLayout.mainDashboardParentRecyclerView.layoutManager =
            LinearLayoutManager(context)
        binding.bottomLayout.mainDashboardParentRecyclerView.adapter =
            HomeParentAdapter(prepareHomeData(), this, requireActivity())


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
            walletDetailVisibility = false
        }

        binding.topLayout.ivUserActivation.setOnClickListener {
//         fragmentListener.onUserProfileClick()
            if (isAccountActive){
                val bottomSheet = MembershipPlanDetails()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            else{
                val bottomSheet = UpgradeToPro()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it


        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getWalletBalance(userID, roleID)
                viewModel.getPCashBalance(userID, roleID)
                viewModel.checkIsAccountActive(userID)
            }

        })
        viewModel.walletBalance.observe(viewLifecycleOwner, Observer { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.walletDetailView.tvWalletBalance.setAmount(it)
                        binding.topLayout.walletBalanceView.tvWalletBalance.setAmount(it)
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
        viewModel.pCash.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.walletDetailView.tvPCash.setAmount(it)
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

        viewModel.isAccountActive.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        isAccountActive = it
                        if (isAccountActive){
                            binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_diamond)

                        }
                        else{
                            binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_up_arrow)

                            val bottomSheet = UpgradeToPro()
                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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


    private fun prepareHomeData(): List<HomeParentModel> {

        val dataList: MutableList<HomeParentModel> = ArrayList()


        val services1: MutableList<ModelServiceView> = java.util.ArrayList()
        services1.add(
            ModelServiceView(
                "Add Money",
                R.drawable.ic_add_money,
                RechargeEnum.ADD_MONEY
            )
        )
        services1.add(
            ModelServiceView(
                "History",
                R.drawable.ic_history,
                RechargeEnum.PAYMENT_HISTORY
            )
        )
        services1.add(ModelServiceView("Wallet", R.drawable.ic_wallet, RechargeEnum.WALLET))
        services1.add(
            ModelServiceView(
                "Online Shopping",
                R.drawable.ic_shopping,
                RechargeEnum.SHOPPING
            )
        )


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
        services2.add(
            ModelServiceView(
                "Electricity",
                R.drawable.ic_electricity,
                RechargeEnum.ELECTRICITY
            )
        )
        services2.add(ModelServiceView("Water", R.drawable.ic_water, RechargeEnum.WATER))
        services2.add(ModelServiceView("Gas Cylinder Booking", R.drawable.ic_gas, RechargeEnum.GAS))
        services2.add(
            ModelServiceView(
                "Broadband",
                R.drawable.ic_broadband,
                RechargeEnum.BROADBAND
            )
        )
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

        val servicesCategory2 = ModelServiceCategory("Featured", services2)

        val model2 = HomeParentModel(MyEnums.SERVICES, servicesCategory2)


        val bannerList: MutableList<ModelBanner> = java.util.ArrayList()
        bannerList.add(ModelBanner("First", R.drawable.banner1, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner2, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner3, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner4, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner5, 1))

        val model3 = HomeParentModel(MyEnums.OFFERS, offerBannerList = bannerList)


        // Working Services
        val workingServices: MutableList<ModelServiceView> = java.util.ArrayList()
        workingServices.add(ModelServiceView("Mobile Recharge", R.drawable.ic_prepaid, RechargeEnum.PREPAID))
        workingServices.add(ModelServiceView("DTH", R.drawable.ic_dth, RechargeEnum.DTH))
        workingServices.add(  ModelServiceView("Electricity", R.drawable.ic_electricity, RechargeEnum.ELECTRICITY))
        workingServices.add(ModelServiceView("Send Money", R.drawable.ic_bank, RechargeEnum.SEND_MONEY))
//        workingServices.add(ModelServiceView("Paytm Wallet Transfer", R.drawable.ic_paytm_logo, RechargeEnum.PAYTM_WALLET_TRANSFER))

        val workingServiceCategory = ModelServiceCategory("Featured", workingServices)

        val workingServiceCategoryModel = HomeParentModel(MyEnums.SERVICES, workingServiceCategory)


        // Coming Soon
        val comingSoonServices: MutableList<ModelServiceView> = java.util.ArrayList()
        /*comingSoonServices.add(
            ModelServiceView(
                "Postpaid",
                R.drawable.ic_postpaid,
                RechargeEnum.POSTPAID
            )
        )*/
        comingSoonServices.add(
            ModelServiceView(
                "Landline",
                R.drawable.ic_landline,
                RechargeEnum.LANDLINE
            )
        )
        /*comingSoonServices.add(
            ModelServiceView(
                "Electricity",
                R.drawable.ic_electricity,
                RechargeEnum.ELECTRICITY
            )
        )*/
        comingSoonServices.add(ModelServiceView("Water", R.drawable.ic_water, RechargeEnum.WATER))
        comingSoonServices.add(
            ModelServiceView(
                "Gas Cylinder Booking",
                R.drawable.ic_gas,
                RechargeEnum.GAS
            )
        )
        comingSoonServices.add(
            ModelServiceView(
                "Broadband",
                R.drawable.ic_broadband,
                RechargeEnum.BROADBAND
            )
        )
        comingSoonServices.add(ModelServiceView("Loans", R.drawable.ic_loan, RechargeEnum.LOAN))
        comingSoonServices.add(ModelServiceView("DMT", R.drawable.ic_dmt, RechargeEnum.DMT))
        comingSoonServices.add(
            ModelServiceView(
                "Life Insurance",
                R.drawable.ic_life_insurance,
                RechargeEnum.LIFE_INSURANCE
            )
        )
        comingSoonServices.add(
            ModelServiceView(
                "FASTag",
                R.drawable.ic_fastag,
                RechargeEnum.FASTAG
            )
        )

        val comingSoonServicesCategory = ModelServiceCategory("Coming Soon", comingSoonServices)

        val comingSoonServicesCategoryModel =
            HomeParentModel(MyEnums.SERVICES, comingSoonServicesCategory)




        dataList.add(model1)
        dataList.add(model3)
//        dataList.add(model2)
        dataList.add(workingServiceCategoryModel)
        dataList.add(comingSoonServicesCategoryModel)
        return dataList
    }

    override fun onItemClick(viewType: MyEnums, action: RechargeEnum) {
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show()
        when (viewType) {
            MyEnums.SERVICES -> performActionForServices(action)
        }
    }

    private fun performActionForServices(action: RechargeEnum) {
        when (action) {
            RechargeEnum.PREPAID, RechargeEnum.POSTPAID ->{
//                findNavController().navigate(R.id.action_home_to_mobileRechargeActivity)
                startActivity(Intent(requireActivity(),NewRechargeActivity::class.java))
            }
            RechargeEnum.DTH -> findNavController().navigate(R.id.action_home_to_dthActivity)
            RechargeEnum.ELECTRICITY -> findNavController().navigate(R.id.action_home_to_electricityActivity)
            RechargeEnum.SHOPPING -> findNavController().navigate(R.id.action_home_to_shop)
            RechargeEnum.WALLET -> {
                val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
                intent.putExtra("FILTER", "BUSINESS")
                startActivity(intent)
            }
            RechargeEnum.ADD_MONEY -> {
                startActivity(Intent(requireActivity(), AddMoneyToWallet::class.java))
            }
            RechargeEnum.PAYMENT_HISTORY -> findNavController().navigate(R.id.action_home_to_paymentHistory)
            RechargeEnum.BANK_TRANSFER, RechargeEnum.PAYTM_WALLET_TRANSFER,RechargeEnum.SEND_MONEY -> {
                startActivity(Intent(requireActivity(), NewPayout::class.java))
            }
            else -> {
//                findNavController().navigate(HomeDirections.actionHomeToRechargeActivity(action))
                showToast("Coming soon !!!")
            }
        }
    }

    interface HomeFragmentListener {
        fun onUserProfileClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarHandler.hide()
    }
}