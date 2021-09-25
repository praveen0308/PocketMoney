package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPayoutTransferMoneyBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayoutTransferMoney : BaseBottomSheetDialogFragment<FragmentPayoutTransferMoneyBinding>(FragmentPayoutTransferMoneyBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var payoutType = 1

    private var userID: String = ""
    private var roleID: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,"Transfer")

        binding.etAmount.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()){
                binding.tilAmount.error = null
                binding.btnSubmit.setState(LoadingButton.LoadingStates.NORMAL,"Transfer")
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

        binding.btnSubmit.setButtonClick {
            val amount = binding.etAmount.text.toString()

            val beneficiary = viewModel.selectedBeneficiary.value!!

            when (payoutType) {
                // Bank transfer
                1 -> {
                    viewModel.initiateBankTransfer(viewModel.customerNumber.value!!,
                    PaytmRequestData(
                        account = beneficiary.Account,
                        ifsc = beneficiary.IFSCCode,
                        amount = amount,
                        email = beneficiary.BeneficiaryName,
                        transfermode = "IMPS"

                    )
                    )
                }
                // Upi Transfer
                2 -> {
                    viewModel.initiateBankTransfer(viewModel.customerNumber.value!!,
                        PaytmRequestData(
                            account = beneficiary.Account,
                            amount = amount,
                            email = beneficiary.BeneficiaryName,
                            transfermode = "UPI"
                        )
                    )
                }
                // Paytm transfer
                3 -> {
                    viewModel.initiateBankTransfer(viewModel.customerNumber.value!!,
                        PaytmRequestData(
                            account = beneficiary.Account,
                            amount = amount,
                            email = beneficiary.BeneficiaryName,
                            transfermode = "PAYTMWALLET"
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
                        showToast("Transferred successfully !!!")
                        dismiss()
                    }
                    displayLoading(false)

                }
                Status.LOADING -> {
                    displayLoading(true)
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,"Processing...")
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        viewModel.upiTransferResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Transferred successfully !!!")
                        dismiss()
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,"Processing...")
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        viewModel.paytmTransferResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Transferred successfully !!!")
                        dismiss()
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,"Processing...")
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

}