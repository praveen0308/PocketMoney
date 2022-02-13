package com.jmm.payout

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.jmm.core.TaskResultDialog
import com.jmm.core.utils.showTaskResultDialog
import com.jmm.model.myEnums.PayoutTransferMode
import com.jmm.model.myEnums.WalletType
import com.jmm.model.payoutmodels.Beneficiary
import com.jmm.model.payoutmodels.PayoutTransactionResponse
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.payout.databinding.FragmentPayoutTransferMoneyBinding
import com.jmm.util.BaseBottomSheetDialogFragment
import com.jmm.util.LoadingButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayoutTransferMoney : BaseBottomSheetDialogFragment<FragmentPayoutTransferMoneyBinding>(FragmentPayoutTransferMoneyBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var payoutType = 1
    private var paymentMode = PayoutTransferMode.BankTransfer

    private var userID: String = ""
    private var userName: String = ""
    private var roleID: Int = 0
    private var walletBalance: Double = 0.0
    private lateinit var beneficiary: Beneficiary
    private lateinit var payoutTransactionResponse: PayoutTransactionResponse
    private var amount = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED, "Transfer")

        binding.etAmount.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.tilAmount.error = null
                binding.btnSubmit.setState(LoadingButton.LoadingStates.NORMAL, "Transfer")
            } else {
                if (text.toString().toDouble() > walletBalance) {
                    binding.tilAmount.error = "You can only transfer money in your wallet."
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,"Transfer")
                }else{
                    when (payoutType) {
                        // Bank transfer

                        1 -> {

                            if (text.toString().toDouble()>=100){
                                binding.tilAmount.error = null
                                binding.btnSubmit.setState(LoadingButton.LoadingStates.NORMAL,"Transfer")
                            }else{
                                binding.tilAmount.error = "You can not transfer less than ₹100"
                                binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,"Transfer")
                            }
                        }
                        else->{
                            if (text.toString().toDouble()>=10){
                                binding.tilAmount.error = null
                                binding.btnSubmit.setState(LoadingButton.LoadingStates.NORMAL,"Transfer")
                            }else{
                                binding.tilAmount.error = "You can not transfer less than ₹10"
                                binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,"Transfer")
                            }
                        }

                    }
                }

            }


        }

        binding.btnSubmit.setButtonClick {
            amount = binding.etAmount.text.toString()

            beneficiary = viewModel.selectedBeneficiary.value!!

            viewModel.initiatePayoutTransfer(
                beneficiary.BeneficiaryID.toString(),
                PaytmRequestData(
                    account = beneficiary.Account,
                    ifsc = beneficiary.IFSCCode,
                    amount = amount,
                    email = beneficiary.BeneficiaryName,
                    transfermode = paymentMode,
                    userid = userID

                )
            )

        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it

        })
        viewModel.userName.observe(viewLifecycleOwner, {
            userName = it
        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getWalletBalance(userID, roleID)
            }
        })

        viewModel.payoutType.observe(viewLifecycleOwner, {
            payoutType = it
            when (payoutType) {
                // Bank transfer
                1 -> {
                    paymentMode = PayoutTransferMode.BankTransfer
                    binding.apply {
                        tvSheetTitle.text = "Bank Transfer"
                        tilAccountNumber.hint = "Bank Account No."
                        tilIfscCode.isVisible = true
                    }
                }
                // Upi Transfer
                2 -> {
                    paymentMode = PayoutTransferMode.UpiTransfer
                    binding.apply {
                        tvSheetTitle.text = "UPI Transfer"
                        tilAccountNumber.hint = "UPI ID"
                        tilIfscCode.isVisible = false
                    }
                }
                // Paytm transfer
                3 -> {
                    paymentMode = PayoutTransferMode.PaytmWalletTransfer
                    binding.apply {
                        tvSheetTitle.text = "Wallet Transfer"
                        tilAccountNumber.hint = "Paytm UPI ID"
                        tilIfscCode.isVisible = false
                    }
                }

            }
        })

        viewModel.selectedBeneficiary.observe(viewLifecycleOwner, { beneficiary ->
            binding.apply {
                etAccountNumber.setText(beneficiary.Account)
                etCustomerName.setText(beneficiary.BeneficiaryName)
            }

            beneficiary.IFSCCode?.let {
                binding.apply {
                    etIfscCode.setText(it)
                }
            }
        })

        viewModel.payoutTransferMoneyPageState.observe(viewLifecycleOwner, { state ->

            when (state) {
                is PayoutTransferState.Idle -> {
                    binding.btnSubmit.setState(
                        LoadingButton.LoadingStates.NORMAL,
                        msg = "Continue"
                    )
                }
                is PayoutTransferState.Loading -> {
                    binding.btnSubmit.setState(
                        LoadingButton.LoadingStates.LOADING,
                        msg = "Processing..."
                    )
                }
                is PayoutTransferState.Error -> {
                    showToast(state.message)
                }
                is PayoutTransferState.GotWalletBalance -> {
                    walletBalance = state.balance
                    binding.tvWalletBalance.text = "Wallet Balance : $walletBalance"
                    viewModel.payoutTransferMoneyPageState.postValue(PayoutTransferState.Idle)
                }
                is PayoutTransferState.PayoutTransactionSuccessful -> {
                    payoutTransactionResponse = state.response
                    viewModel.sendSmsOfTransaction(
                        userID, userName, binding.etAmount.text.toString(),
                        beneficiary.BeneficiaryName!!,
                        beneficiary.Account!!,
                        paymentMode
                    )
                }

                is PayoutTransferState.PayoutTransactionFailed -> {
                    payoutTransactionResponse = state.response
                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Transaction failed!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        payoutTransactionResponse.statusMessage
                    )
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.FAILURE)
                    bundle.putString(
                        TaskResultDialog.KEY_REF_ID,
                        payoutTransactionResponse.result!!.paytmOrderId
                    )
                    bundle.putString(TaskResultDialog.KEY_ACCOUNT_NUMBER, beneficiary.Account)
                    bundle.putString(TaskResultDialog.KEY_AMOUNT, amount)
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        payoutTransactionResponse.status
                    )
                    bundle.putInt(TaskResultDialog.KEY_WALLET_TYPE_ID, WalletType.Wallet.id)
                    showTaskResultDialog(bundle, requireActivity().supportFragmentManager)
                    dismiss()
                }
                is PayoutTransferState.SMSGenerationFailed -> {
                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Transaction successful!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        "₹${binding.etAmount.text.toString()} transferred to ${viewModel.selectedBeneficiary.value!!.BeneficiaryName} successfully!!"
                    )
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.SUCCESS)
                    bundle.putString(
                        TaskResultDialog.KEY_REF_ID,
                        payoutTransactionResponse.result!!.paytmOrderId
                    )
                    bundle.putString(TaskResultDialog.KEY_ACCOUNT_NUMBER, beneficiary.Account)
                    bundle.putString(TaskResultDialog.KEY_AMOUNT, amount)
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        payoutTransactionResponse.status
                    )
                    bundle.putInt(TaskResultDialog.KEY_WALLET_TYPE_ID, WalletType.Wallet.id)
                    showTaskResultDialog(bundle, requireActivity().supportFragmentManager)

                    dismiss()
                }
                is PayoutTransferState.SentSMS -> {

                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Transaction successful!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        "₹${binding.etAmount.text.toString()} transferred to ${viewModel.selectedBeneficiary.value!!.BeneficiaryName} successfully!!"
                    )
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.SUCCESS)
                    bundle.putString(
                        TaskResultDialog.KEY_REF_ID,
                        payoutTransactionResponse.result!!.paytmOrderId
                    )
                    bundle.putString(TaskResultDialog.KEY_ACCOUNT_NUMBER, beneficiary.Account)
                    bundle.putString(TaskResultDialog.KEY_AMOUNT, amount)
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        payoutTransactionResponse.status
                    )
                    bundle.putInt(TaskResultDialog.KEY_WALLET_TYPE_ID, WalletType.Wallet.id)
                    showTaskResultDialog(bundle, requireActivity().supportFragmentManager)

                    dismiss()

                }
            }
        })
    }

    override fun onDestroyView() {
        viewModel.payoutTransferMoneyPageState.postValue(PayoutTransferState.Idle)
        super.onDestroyView()

    }
}
