package com.sampurna.pocketmoney.mlm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.sampurna.pocketmoney.databinding.ActivityAddMoneyToWalletBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.PMWalletModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.mlm.viewmodel.AddMoneyToWalletViewModel
import com.sampurna.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddMoneyToWallet : BaseActivity<ActivityAddMoneyToWalletBinding>(ActivityAddMoneyToWalletBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, PaytmPaymentTransactionCallback {

    private lateinit var gatewayOrderId : String
    private lateinit var mAmount : String
    private var userId = ""

    private val viewModel by viewModels<AddMoneyToWalletViewModel>()

    private lateinit var paytmResponseModel: PaytmResponseModel

    private var requestId : String = ""
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


        binding.btnPay.setButtonClick {
            if(userId.isEmpty()){
                checkAuthorization()
            }else{
                gatewayOrderId = createRandomOrderId()
                mAmount = binding.etAmount.text.toString()
                viewModel.initiateTransactionApi(
                    PaytmRequestData(
                        account= gatewayOrderId,
                        amount = mAmount,
                        callbackurl = Constants.PAYTM_CALLBACK_URL,
                        userid = userId
                    )
                )
            }

        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
            if(userId.isEmpty()){
                checkAuthorization()
            }
        })

        // step 1
        viewModel.checkSum.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val paytmOrder = PaytmOrder(
                            gatewayOrderId,
                            Constants.P_MERCHANT_ID,
                            it,
                            mAmount,
                            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$gatewayOrderId"
                        )
                        processPaytmTransaction(paytmOrder)
                    }
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing..")
                }
                Status.ERROR -> {
                    respondButton(LoadingButton.LoadingStates.NORMAL,msg = "Pay..")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        // step 2
        viewModel.addCustWalletDetailResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        requestId = it
                        viewModel.addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
                                ReferenceTransactionId = requestId,
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
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing..")
                }
                Status.ERROR -> {
                    respondButton(LoadingButton.LoadingStates.NORMAL,msg = "Pay..")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        // step 3
        viewModel.addPaymentTransResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (paytmResponseModel.STATUS == "SUCCESS"){
                            viewModel.actionOnWalletDetail(requestId, "Updated as online payment success","Approve",paytmResponseModel.PAYMENTMODE.toString())
//                            viewModel.addCompanyTransactionResponse(userId,userId,paytmResponseModel.TXNAMOUNT!!.toDouble(),1,1,it,"0")
                        }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                            viewModel.actionOnWalletDetail(requestId, "Updated as online payment failed","Rejected",paytmResponseModel.PAYMENTMODE.toString())
                        }
                    }
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing..")
                }
                Status.ERROR -> {
                    respondButton(LoadingButton.LoadingStates.NORMAL,msg = "Pay..")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        viewModel.actionOnWalletDetailResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (paytmResponseModel.STATUS == "SUCCESS"){
                            showSuccessfulDialog("Money added successfully !!!!",dialogListener = object : MyDialogListener{
                                override fun onDismiss() {
                                    finish()
                                }
                            })

                        }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                            showToast("Failed !!!")
                        }

                    }
                    respondButton(LoadingButton.LoadingStates.NORMAL,msg = "Pay..")
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing..")
                }
                Status.ERROR -> {
                    respondButton(LoadingButton.LoadingStates.NORMAL,msg = "Pay..")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
    }
    private fun respondButton(state: LoadingButton.LoadingStates, mText:String="", msg:String=""){
        binding.btnPay.setState(state, mText, msg)
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
        Timber.d("onTransactionResponse : ${p0.toString()}")
        p0?.let {
            paytmResponseModel = PaytmResponseModel(
                STATUS = it.getString("STATUS")!!.substring(4),
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
        when(paytmResponseModel.STATUS){
            "SUCCESS"->{
                viewModel.addCustomerWalletDetails(PMWalletModel(
                    USER_ID = userId.toDouble(),
                    MODE_TYPE_ID = 5,
                    PAID_AMOUNT = mAmount.toDouble(),
                    PAID_DATE = getTodayDate(),
                    CON_AC_NO = (23225151).toDouble(),
                    MODE_NO = "PAYTM GATEWAY",
                    NARRATIONS = "Initiated transaction adding money in wallet"

                ))
            }
            "FAILURE"->{
                showToast("Transaction Failed !!!")
            }
            "CANCELLED"->{
                showToast("Transaction Cancelled !!!")
            }
            else->{
                showToast("Something went wrong !!!")
            }
        }


    }
    override fun networkNotAvailable() {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }

}