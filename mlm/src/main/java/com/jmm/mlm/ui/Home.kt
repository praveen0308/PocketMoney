package com.jmm.mlm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.core.HomeParentItemListener
import com.jmm.core.utils.setAmount
import com.jmm.membership.MembershipPlanDetails
import com.jmm.membership.UpgradeToPro
import com.jmm.mlm.MainDashboard
import com.jmm.mlm.R
import com.jmm.mlm.adapters.HomeParentAdapter
import com.jmm.mlm.databinding.FragmentHomeBinding
import com.jmm.mlm.viewmodel.HomePageState
import com.jmm.mlm.viewmodel.HomeViewModel
import com.jmm.model.RechargeEnum
import com.jmm.model.myEnums.MyEnums
import com.jmm.navigation.NavRoute.AddMoneyToWallet
import com.jmm.navigation.NavRoute.DthActivity
import com.jmm.navigation.NavRoute.GooglePlayRecharge
import com.jmm.navigation.NavRoute.NewPayout
import com.jmm.navigation.NavRoute.NewRechargeActivity
import com.jmm.repository.IResource
import com.jmm.repository.WalletRepository
import com.jmm.util.BaseFragment
import com.jmm.util.connection.ConnectionLiveData
import com.jmm.util.identify
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class Home : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    HomeParentItemListener {

    // ViewModel
    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var walletRepo: WalletRepository

    // Interface
    private lateinit var fragmentListener: HomeFragmentListener

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    //Variables
    private var walletDetailVisibility: Boolean = false
    private var userID: String = ""
    private var roleID: Int = 0

    private var isAccountActive = false


    private lateinit var homeParentAdapter: HomeParentAdapter

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        fragmentListener = activity as MainDashboard
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvDashboardItems()
//        viewModel.getStoreBanners()
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
            if (isAccountActive) {
                val bottomSheet = MembershipPlanDetails()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            } else {
                val bottomSheet = UpgradeToPro()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

        }

    }

    private fun setupRvDashboardItems() {
        homeParentAdapter = HomeParentAdapter(this, requireActivity())
        binding.bottomLayout.mainDashboardParentRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = homeParentAdapter

        }


    }

    override fun subscribeObservers() {
        /*binding.topLayout.walletBalanceView.textView7.text=
            when(connectionLiveData.hasActiveObservers()){
                true -> "Network Connection Established"
                false -> "No Internet"
            }*/
        /*connectionLiveData.observe(viewLifecycleOwner,{
            binding.topLayout.walletBalanceView.textView7.text = when(it){
                true -> "Network Connection Established"
                false -> "No Internet"
            }
        })
*/
        viewModel.storeBanners.observe(viewLifecycleOwner) {
            homeParentAdapter.setHomeParentItems(viewModel.prepareHomeData(it.data!!.toMutableList()))
        }

        viewModel.userId.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                checkAuthorization()
            } else {
                userID = it
                viewModel.userType.observe(viewLifecycleOwner) {
                    isAccountActive = it
                    if (isAccountActive) {
                        binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_diamond)
                    } else {
                        binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_up_arrow)
                        val bottomSheet = com.jmm.membership.UpgradeToPro()
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    }
                }
            }
        }

        viewModel.userRoleID.observe(viewLifecycleOwner) {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getDashboardData(userID,roleID)
//                viewModel.getCustomerBalanceWithAuth(userID, roleID)

            }

        }

        viewModel.accountData.observe(viewLifecycleOwner) { result ->
            displayLoading(false)
            when (result) {
                is IResource.Error -> {
                    showToast(result.error!!.identify())
                    Timber.e(result.error)
                }
                is IResource.Loading -> displayLoading(true)
                is IResource.Success -> {
                    result.data?.let {


                        binding.walletDetailView.tvWalletBalance.setAmount(it.BusinessWallet)
                        binding.walletDetailView.tvPCash.setAmount(it.IncomeWallet)
                        binding.topLayout.walletBalanceView.tvWalletBalance.setAmount(it.BusinessWallet)

                    }

                }
            }

        }
        viewModel.homePageState.observe(viewLifecycleOwner) { state ->
            displayLoading(false)
            when (state) {
                HomePageState.Loading -> displayLoading(true)
                is HomePageState.Error -> showToast(state.msg)
                is HomePageState.ReceivedUserData -> {
                   /* state.data.BusinessWallet?.let {
                        binding.walletDetailView.tvWalletBalance.setAmount(
                            it
                        )
                    }
                    state.data.BusinessWallet?.let {
                        binding.topLayout.walletBalanceView.tvWalletBalance.setAmount(
                            it
                        )
                    }
                    state.data.IncomeWallet?.let {
                        binding.walletDetailView.tvPCash.setAmount(
                            it
                        )
                    }*/

                }
                is HomePageState.ReceivedBanners -> {
                    homeParentAdapter.setHomeParentItems(viewModel.prepareHomeData(state.banners.toMutableList()))
                }

            }

        }


    }


    override fun onItemClick(viewType: MyEnums, action: RechargeEnum) {
        when (viewType) {
            MyEnums.SERVICES -> performActionForServices(action)
        }
    }

    private fun performActionForServices(action: RechargeEnum) {
        when (action) {
            RechargeEnum.PREPAID, RechargeEnum.POSTPAID -> {
//                findNavController().navigate(R.id.action_home_to_mobileRechargeActivity)
                startActivity(Intent(requireActivity(), Class.forName(NewRechargeActivity)))
            }
            RechargeEnum.DTH -> {
//                findNavController().navigate(R.id.action_home_to_dthActivity)
                startActivity(Intent(requireActivity(), Class.forName(DthActivity)))
            }
            RechargeEnum.GOOGLE_PLAY_RECHARGE -> {
//                findNavController().navigate(R.id.action_home_to_googlePlayRecharge)
                startActivity(Intent(requireActivity(), Class.forName(GooglePlayRecharge)))
            }
            RechargeEnum.ELECTRICITY -> {
//                findNavController().navigate(R.id.action_home_to_electricityActivity)
            }
            RechargeEnum.SHOPPING -> {
//                findNavController().navigate(R.id.action_home_to_shop)
                findNavController().navigate(HomeDirections.actionHomeToShop())
            }
            RechargeEnum.WALLET -> {
                val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
                intent.putExtra("FILTER", "BUSINESS")
                startActivity(intent)
            }
            RechargeEnum.ADD_MONEY -> {
                startActivity(Intent(requireActivity(), Class.forName(AddMoneyToWallet)))
            }
            RechargeEnum.PAYMENT_HISTORY -> {
//                findNavController().navigate(R.id.action_home_to_paymentHistory)
                findNavController().navigate(HomeDirections.actionHomeToPaymentHistoryFlow())
            }
            RechargeEnum.BANK_TRANSFER, RechargeEnum.PAYTM_WALLET_TRANSFER, RechargeEnum.SEND_MONEY -> {
                startActivity(Intent(requireActivity(), Class.forName(NewPayout)))
            }
            else -> {
//                findNavController().navigate(HomeDirections.actionHomeToRechargeActivity(action))
                showToast("Coming soon !!!")
                /*val intent = Intent(requireActivity(), Class.forName(KycActivity))
                startActivity(intent)*/
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