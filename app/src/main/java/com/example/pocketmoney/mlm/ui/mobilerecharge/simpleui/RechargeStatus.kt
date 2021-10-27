package com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentRechargeStatusBinding
import com.example.pocketmoney.mlm.model.OperationResultModel
import com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui.Recharge.Companion.PENDING
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.getTodayDate
import com.example.pocketmoney.utils.myEnums.WalletType
import com.example.pocketmoney.utils.setAmount
import com.example.pocketmoney.utils.showActionDialog
import com.jmm.brsap.dialog_builder.DialogType

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RechargeStatus :
    BaseFragment<FragmentRechargeStatusBinding>(FragmentRechargeStatusBinding::inflate) {

    private val viewModel by activityViewModels<MobileRechargeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as NewRechargeActivity).setToolbarVisibility(false)
        binding.apply {
            ivClose.setOnClickListener {
                requireActivity().finish()
            }
            btnContinue.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    override fun subscribeObservers() {

        viewModel.progressStatus.observe(viewLifecycleOwner, {
            displayLoading(false)
            when (it) {
                Recharge.LOADING -> {
                    hideLoadingDialog()
                    showLoadingDialog("Processing...")
                }
                Recharge.ERROR -> {
                    showActionDialog(requireActivity(),DialogType.ERROR,"Oops!",
                        "Something went wrong!! Check your internet connection.",mListener = {
                            requireActivity().finish()
                        })
                }
                Recharge.INITIATING_RECHARGE_SERVICE->{
                    showLoadingDialog("Requesting for recharge...")
                }
                PENDING -> {
                    populateRechargeStatusUi(PENDING)
                }
                Recharge.RECHARGE_FAILED -> {
                    populateRechargeStatusUi(Recharge.RECHARGE_FAILED)
                }
                Recharge.RECHARGE_SUCCESSFUL -> {
                    populateRechargeStatusUi(Recharge.RECHARGE_SUCCESSFUL)
                }
            }
        })
    }


    private fun populateRechargeStatusUi(status:Int){
        hideLoadingDialog()
        displayLoading(false)
        when(status){
            PENDING -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.Orange
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_baseline_warning_24)
                    tvTitle.text = "Recharge pending!"
                    tvSubtitle.text =
                        "Your ${viewModel.selectedOperator.value} prepaid recharge is pending."
                }
            }
            Recharge.RECHARGE_FAILED -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.CreamyRed
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_baseline_error_24)
                    tvTitle.text = "Recharge failed!"
                    tvSubtitle.text =
                        "Your ${viewModel.selectedOperator.value} prepaid was unfortunately failed."

                }

            }
            Recharge.RECHARGE_SUCCESSFUL -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.success
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_round_check_24)
                    tvTitle.text = "Recharge successful!"
                    tvSubtitle.text =
                        "Your ${viewModel.selectedOperator.value} prepaid was successfully recharged."

                }
            }
        }

        binding.apply {
            when (viewModel.recharge.WalletTypeID) {
                WalletType.Wallet.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_wallet)
                    tvPaidUsing.text = "Paid using wallet"
                }
                WalletType.PCash.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_loan)
                    tvPaidUsing.text = "Paid using pcash"

                }
                WalletType.OnlinePayment.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_paytm_logo)
                    tvPaidUsing.text = "Paid using gateway"
                }
            }
            tvCurrentBalance.text = viewModel.rechargeApiResponse.Status
            tvCurrentBalanceOf.text = "Payment status"
            tvTransactionOf.text = "₹${viewModel.rechargeAmount.value} Recharge"
            tvAmount.setAmount(viewModel.rechargeAmount.value!!)
            tvRecipient.text = viewModel.recharge.MobileNo.toString()
            tvDate.text = getTodayDate()
            tvRefId.text = "Ref ID:${viewModel.recharge.RequestID}"

        }

        binding.root.isVisible = true
    }
}