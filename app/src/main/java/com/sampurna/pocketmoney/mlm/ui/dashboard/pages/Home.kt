package com.sampurna.pocketmoney.mlm.ui.dashboard.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentHomeBinding
import com.sampurna.pocketmoney.mlm.HomeParentItemListener
import com.sampurna.pocketmoney.mlm.adapters.HomeParentAdapter
import com.sampurna.pocketmoney.mlm.model.RechargeEnum
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.mlm.ui.AddMoneyToWallet
import com.sampurna.pocketmoney.mlm.ui.dashboard.CustomerWalletActivity
import com.sampurna.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.sampurna.pocketmoney.mlm.ui.membership.MembershipPlanDetails
import com.sampurna.pocketmoney.mlm.ui.membership.UpgradeToPro
import com.sampurna.pocketmoney.mlm.ui.mobilerecharge.simpleui.NewRechargeActivity
import com.sampurna.pocketmoney.mlm.ui.payouts.NewPayout
import com.sampurna.pocketmoney.mlm.viewmodel.HomePageState
import com.sampurna.pocketmoney.mlm.viewmodel.HomeViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.connection.ConnectionLiveData
import com.sampurna.pocketmoney.utils.myEnums.MyEnums
import com.sampurna.pocketmoney.utils.setAmount
import dagger.hilt.android.AndroidEntryPoint
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
            HomeParentAdapter(viewModel.prepareHomeData(), this, requireActivity())


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
        viewModel.userId.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                checkAuthorization()
            } else {
                userID = it
                viewModel.userType.observe(viewLifecycleOwner, {
                    isAccountActive = it
                    if (isAccountActive) {
                        binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_diamond)
                    } else {
                        binding.topLayout.ivUserActivation.setImageResource(R.drawable.ic_up_arrow)
                        val bottomSheet = UpgradeToPro()
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    }
                })
            }
        })

        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getWalletBalance(userID, roleID)
                viewModel.getPCashBalance(userID, roleID)
            }

        })

        viewModel.homePageState.observe(viewLifecycleOwner, { state ->
            displayLoading(false)
            when (state) {
                is HomePageState.Loading -> displayLoading(state.isLoading)
                is HomePageState.Error -> showToast(state.msg)
                is HomePageState.GotWalletBalance -> {
                    binding.walletDetailView.tvWalletBalance.setAmount(state.balance)
                    binding.topLayout.walletBalanceView.tvWalletBalance.setAmount(state.balance)
                }
                is HomePageState.GotPCashBalance -> {
                    binding.walletDetailView.tvPCash.setAmount(state.balance)
                }
            }

        })


    }




    override fun onItemClick(viewType: MyEnums, action: RechargeEnum) {
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
            RechargeEnum.GOOGLE_PLAY_RECHARGE -> findNavController().navigate(R.id.action_home_to_googlePlayRecharge)
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