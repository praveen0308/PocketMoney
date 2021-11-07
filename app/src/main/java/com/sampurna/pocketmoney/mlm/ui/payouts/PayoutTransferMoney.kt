package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.sampurna.pocketmoney.databinding.FragmentPayoutTransferMoneyBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.*
import com.jmm.brsap.dialog_builder.DialogType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayoutTransferMoney : BaseBottomSheetDialogFragment<FragmentPayoutTransferMoneyBinding>(FragmentPayoutTransferMoneyBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var payoutType = 1

    private var userID: String = ""
    private var roleID: Int = 0
    private var walletBalance: Double = 0.0
    private var isTransferred = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,"Transfer")

        binding.etAmount.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()){
                binding.tilAmount.error = null
                binding.btnSubmit.setState(LoadingButton.LoadingStates.NORMAL,"Transfer")
            }else{
                if (text.toString().toDouble()>walletBalance){
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
            val amount = binding.etAmount.text.toString()

            val beneficiary = viewModel.selectedBeneficiary.value!!
            isTransferred= true
            when (payoutType) {
                // Bank transfer
                1 -> {
                    viewModel.initiateBankTransfer(beneficiary.BeneficiaryID.toString(),
                    PaytmRequestData(
                        account = beneficiary.Account,
                        ifsc = beneficiary.IFSCCode,
                        amount = amount,
                        email = beneficiary.BeneficiaryName,
                        transfermode = "IMPS",
                        userid = userID

                    )
                    )
                }
                // Upi Transfer
                2 -> {
                    viewModel.initiateUpiTransfer(beneficiary.BeneficiaryID.toString(),
                        PaytmRequestData(
                            account = beneficiary.Account,
                            amount = amount,
                            email = beneficiary.BeneficiaryName,
                            transfermode = "UPI",
                            userid = userID

                        )
                    )
                }
                // Paytm transfer
                3 -> {
                    viewModel.initiatePaytmTransfer(beneficiary.BeneficiaryID.toString(),
                        PaytmRequestData(
                            account = beneficiary.Account,
                            amount = amount,
                            email = beneficiary.BeneficiaryName,
                            transfermode = "PAYTMWALLET",
                            userid = userID
                        )
                    )
                }

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
            }
        })

        viewModel.walletBalance.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        walletBalance=it
                        binding.tvWalletBalance.text = "Wallet Balance : $it"
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
        viewModel.payoutType.observe(viewLifecycleOwner,{
            payoutType = it
            when(payoutType){
                // Bank transfer
                1->{
                    binding.apply {
                        tvSheetTitle.text = "Bank Transfer"
                        tilAccountNumber.setHint("Bank Account No.")
                        tilIfscCode.isVisible = true
                    }
                }
                // Upi Transfer
                2->{
                    binding.apply {
                        tvSheetTitle.text = "UPI Transfer"
                        tilAccountNumber.hint = "UPI ID"
                        tilIfscCode.isVisible = false
                    }
                }
                // Paytm transfer
                3->{
                    binding.apply {
                        tvSheetTitle.text = "Wallet Transfer"
                        tilAccountNumber.hint = "Paytm UPI ID"
                        tilIfscCode.isVisible = false
                    }
                }

            }
        })

        viewModel.selectedBeneficiary.observe(viewLifecycleOwner,{beneficiary->

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

        viewModel.bankTransferResponse.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (isTransferred){
                            dismiss()
//                            binding.btnSubmit.setState(LoadingButton.LoadingStates.SUCCESS,msg = "Transaction successful!")
                            showActionDialog(requireActivity(), DialogType.SUCCESS,
                                "Transaction successful!",
                                "₹${binding.etAmount.text.toString()} transferred to ${viewModel.selectedBeneficiary.value!!.BeneficiaryName} successffully!!",
                                "Continue"
                            ) {
                               dismiss()
                            }

                        }

                    }

                }
                Status.LOADING -> {

                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                }
                Status.ERROR -> {
                    _result.message?.let {
                        displayError(it)
                        binding.btnSubmit.setState(LoadingButton.LoadingStates.RETRY,"Retry")
                    }
                }
            }
        })

        viewModel.upiTransferResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (isTransferred){
                            dismiss()
//                            binding.btnSubmit.setState(LoadingButton.LoadingStates.SUCCESS,msg = "Transaction successful!")
                            showActionDialog(requireActivity(), DialogType.SUCCESS,
                                "Transaction successful!",
                                "₹${binding.etAmount.text.toString()} transferred to ${viewModel.selectedBeneficiary.value!!.BeneficiaryName} successffully!!",
                                "Continue"
                            ) {
                                dismiss()
                            }

                        }
                    }

                }
                Status.LOADING -> {

                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                }
                Status.ERROR -> {

                    _result.message?.let {
                        displayError(it)
                        binding.btnSubmit.setState(LoadingButton.LoadingStates.RETRY,"Retry")
                    }
                }
            }
        })

        viewModel.paytmTransferResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (isTransferred){
                            dismiss()
//                            binding.btnSubmit.setState(LoadingButton.LoadingStates.SUCCESS,msg = "Transaction successful!")
                            showActionDialog(requireActivity(), DialogType.SUCCESS,
                                "Transaction successful!",
                                "₹${binding.etAmount.text.toString()} transferred to ${viewModel.selectedBeneficiary.value!!.BeneficiaryName} successffully!!",
                                "Continue"
                            ) {
                                dismiss()
                            }

                        }
                    }
                }
                Status.LOADING -> {
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                }
                Status.ERROR -> {
                    _result.message?.let {
                        displayError(it)
                        binding.btnSubmit.setState(LoadingButton.LoadingStates.RETRY,"Retry")
                    }
                }
            }
        })
    }

}