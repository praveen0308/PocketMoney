package com.example.pocketmoney.mlm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.pocketmoney.databinding.ActivityAddMoneyToWalletBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.viewmodel.AddMoneyToWalletViewModel
import com.example.pocketmoney.utils.*
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddMoneyToWallet : BaseActivity<ActivityAddMoneyToWalletBinding>(ActivityAddMoneyToWalletBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, PaytmPaymentTransactionCallback {

    private lateinit var ACCOUNT_ID : String
    private lateinit var AMOUNT : String
    private lateinit var userId : String

    private val viewModel by viewModels<AddMoneyToWalletViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarAddMoneyToWallet.setApplicationToolbarListener(this)
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
            ACCOUNT_ID = createRandomOrderId()
            AMOUNT = binding.etAmount.text.toString()
            viewModel.initiateTransactionApi(
                PaytmRequestData(
                    account= ACCOUNT_ID,
                    amount = AMOUNT,
                    callbackurl = Constants.PAYTM_CALLBACK_URL,
                    userid = userId
                )
            )
        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })
        viewModel.checkSum.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        val paytmOrder = PaytmOrder(
                            ACCOUNT_ID,
                            Constants.P_MERCHANT_ID,
                            it,
                            AMOUNT,
                            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$ACCOUNT_ID"
                        )
                        processPaytmTransaction(paytmOrder)
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

    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {

            val transactionManager =
                TransactionManager(paytmOrder, this)
            transactionManager.setAppInvokeEnabled(false)
            transactionManager.startTransaction(this, 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    override fun onTransactionResponse(p0: Bundle?) {

    }

    override fun networkNotAvailable() {

    }

    override fun onErrorProceed(p0: String?) {

    }

    override fun clientAuthenticationFailed(p0: String?) {

    }

    override fun someUIErrorOccurred(p0: String?) {

    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {

    }

    override fun onBackPressedCancelTransaction() {

    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        Timber.d(p0)
        showToast("Transaction Cancelled !!!")
    }
}