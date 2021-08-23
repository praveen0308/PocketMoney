package com.example.pocketmoney.mlm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.pocketmoney.databinding.ActivityAddMoneyToWalletBinding
import com.example.pocketmoney.mlm.model.serviceModels.PMWalletModel
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
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

    private lateinit var paytmResponseModel: PaytmResponseModel

    private var RequestId : String = ""
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


        viewModel.addCustWalletDetailResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        viewModel.addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
                                ReferenceTransactionId = it,
                                ServiceTypeId = 1,
                                WalletTypeId = 1,
                                TxnAmount = paytmResponseModel.TXNAMOUNT,
                                Currency = paytmResponseModel.CURRENCY,
                                TransactionTypeId = 1,
                                IsCredit =  false,
                                TxnId = paytmResponseModel.TXNID,
                                Status = paytmResponseModel.STATUS,
                                RespCode = paytmResponseModel.RESPCODE,
                                RespMsg = paytmResponseModel.RESPMSG,
                                BankTxnId = paytmResponseModel.BANKTXNID,
                                BankName = paytmResponseModel.GATEWAYNAME,
                                PaymentMode = paytmResponseModel.PAYMENTMODE
                            )
                        )
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
        p0?.let {
            paytmResponseModel = PaytmResponseModel(
                STATUS = it.getString("STATUS")!!.substring(3),
                ORDERID = it.getString("ORDERID"),
                CHARGEAMOUNT = it.getString("CHARGEAMOUNT"),
                TXNAMOUNT = it.getString("TXNAMOUNT"),
                TXNDATE = it.getString("TXNDATE"),
                MID = it.getString("MID"),
                TXNID = it.getString("TXNID"),
                RESPCODE = it.getString("RESPCODE"),
                PAYMENTMODE = it.getString("PAYMENTMODE"),
                BANKTXNID = it.getString("BANKTXNID"),
                CURRENCY = it.getString("CURRENCY"),
                GATEWAYNAME = it.getString("GATEWAYNAME"),
                RESPMSG = it.getString("RESPMSG")

            )
        }


        val transactionStatus = p0!!.getString("STATUS")!!.substring(3)
        showToast(transactionStatus)
        if (transactionStatus=="SUCCESS"){
        viewModel.addCustomerWalletDetails(PMWalletModel(
            USER_ID = userId.toDouble(),
            MODE_TYPE_ID = 5,
            PAID_AMOUNT = AMOUNT.toDouble(),
            PAID_DATE = getTodayDate(),
            CON_AC_NO = (23225151).toDouble(),
            MODE_NO = "PAYTM GATEWAY",
            NARRATIONS = "Initiated transaction adding money in wallet"

        ))}
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