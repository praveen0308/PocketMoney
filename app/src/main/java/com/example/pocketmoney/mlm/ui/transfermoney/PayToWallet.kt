package com.example.pocketmoney.mlm.ui.transfermoney

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPayToWalletBinding
import com.example.pocketmoney.mlm.model.CustomerDetailResponse
import com.example.pocketmoney.mlm.model.OperationResultModel
import com.example.pocketmoney.mlm.viewmodel.B2BTransferViewModel
import com.example.pocketmoney.paymentgateway.OperationResultDialog
import com.example.pocketmoney.utils.*
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayToWallet : BaseFragment<FragmentPayToWalletBinding>(FragmentPayToWalletBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener,
    OperationResultDialog.OperationResultDialogCallback {

    private val viewModel by activityViewModels<B2BTransferViewModel>()

    private lateinit var userId:String
    private var roleId = 0
    private lateinit var recipientUserId:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarPayToWallet.setApplicationToolbarListener(this)
        binding.etAmount.addTextChangedListener {
            if (it.toString().isNotEmpty()){
                if(it.toString().toInt()!=0){
                    binding.btnPay.isVisible = true
                }
            }else{
                binding.btnPay.isVisible = false
            }
        }
        binding.btnPay.setOnClickListener {
            binding.btnPay.isEnabled = false
            viewModel.getWalletBalance(userId, roleId)

        }
    }
    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userId = it
        })

        viewModel.userRoleID.observe(viewLifecycleOwner,{
            roleId = it
        })
        viewModel.recipientUserId.observe(viewLifecycleOwner,{
            recipientUserId = it
            viewModel.getCustomerDetail(recipientUserId)
        })

        viewModel.customerDetail.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.groupUserDetail.isVisible = true
                        binding.groupEnterAmount.isVisible = true
                        populateCustomerDetails(it)
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

        viewModel.walletBalance.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it<binding.etAmount.text.toString().toDouble()){
                            showToast("Insufficient Balance !!!")
                            binding.btnPay.isEnabled = true
                        }else{
                            val requestData = JsonObject()
                            requestData.addProperty("TransferBy",userId)
                            requestData.addProperty("TransferTo",recipientUserId)
                            requestData.addProperty("TransferAmount",binding.etAmount.text.toString().toDouble())
                            requestData.addProperty("TransferComment",binding.etMessage.text.toString())
                            viewModel.b2bTransfer(requestData)
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


        viewModel.b2bTransferResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        showFullScreenDialog(
                            OperationResultModel(title1 = "Transaction done successfully !!!",
                                amount = binding.etAmount.text.toString(),status = "Payment Successful",
                                timestamp = getTodayDate(),it.toString(),animationUrl = R.raw.success_animation)
                            ,operationResultDialogCallback = this)

                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {

                        showFullScreenDialog(
                            OperationResultModel(title1 = "Transaction failed !!!",
                                amount = binding.etAmount.text.toString(),status = "Payment failed.",
                                timestamp = getTodayDate(),"",animationUrl = R.raw.error_animation)
                            ,operationResultDialogCallback = this)
                        displayError(it)
                    }
                }
            }
        })


    }

    private fun populateCustomerDetails(it: CustomerDetailResponse) {
        binding.apply {
            tvUsername.text = it.FullName
            tvUserid.text = it.UserID
        }
    }

    override fun onToolbarNavClick() {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        requireActivity().finish()

                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }
        showAlertDialog("Do really want to cancel payment?", dialogClickListener)

    }

    override fun onMenuClick() {

    }

    override fun onResultDialogDismiss() {
        requireActivity().finish()
    }

}