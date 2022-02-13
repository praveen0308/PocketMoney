package com.jmm.play_recharge

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.core.TaskResultDialog
import com.jmm.core.utils.Constants
import com.jmm.core.utils.createRandomOrderId
import com.jmm.core.utils.showActionDialog
import com.jmm.core.utils.showTaskResultDialog
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.play_recharge.databinding.ActivityGooglePlayRechargeBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GooglePlayRecharge :
    BaseActivity<ActivityGooglePlayRechargeBinding>(ActivityGooglePlayRechargeBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, PaytmPaymentTransactionCallback,
    PaymentMethods.PaymentMethodsInterface {

    private val viewModel by viewModels<PlayRechargeViewModel>()
    private var gatewayOrderId: String = ""
    private var userId: String = ""
    private var roleId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarPlayRecharge.setApplicationToolbarListener(this)


        binding.etMobileNumber.doOnTextChanged { text, start, before, count ->
            if (text.toString().length == 10) {
                viewModel.rechargeMobileNo.postValue(text.toString())

            }
        }

        binding.btnConfirm.setOnClickListener {
            if(userId.isEmpty()){
                checkAuthorization()
            }else{
                val number = binding.etMobileNumber.text.toString().trim()
                viewModel.rechargeMobileNo.postValue(number)
                val amount = binding.etAmount.text.toString()
                if (amount.isNullOrEmpty()) {
                    showToast("Enter valid recharge amount!!!")
                } else {
                    if (amount.toInt() > 0) {
                        viewModel.rechargeAmount.postValue(amount.toInt())
                        val sheet = PaymentMethods(this, isOnline = false)
                        sheet.show(supportFragmentManager, sheet.tag)
                    } else {
                        showToast("Enter valid recharge amount!!!")
                    }
                }

            }

        }
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userId = it
        }

        viewModel.userRoleID.observe(this) {
            roleId = it
        }


        viewModel.rechargeAmount.observe(this) {
            binding.etAmount.setText(it.toString())
        }


        viewModel.rechargeMobileNo.observe(this) {

            if (it.length == 10) binding.btnConfirm.isEnabled = true
        }
        viewModel.pageState.observe(this) { state ->

            displayLoading(false)
            when (state) {
                is PlayRechargePageState.Error -> {
                    showToast(state.msg)
                    hideLoadingDialog()
                }
                PlayRechargePageState.Idle -> {}
                PlayRechargePageState.InsufficientBalance -> {
                    hideLoadingDialog()
                    showActionDialog(
                        this,
                        DialogType.ERROR,
                        "Insufficient balance!!",
                        "Your wallet balance is low.",
                        "Okay"
                    ) {
                        // do things here
                    }
                }
                PlayRechargePageState.Loading -> displayLoading(true)
                is PlayRechargePageState.Processing -> showLoadingDialog(state.msg)
                is PlayRechargePageState.ReceivedCheckSum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.P_MERCHANT_ID,
                        state.token,
                        viewModel.rechargeAmount.value.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }

                PlayRechargePageState.RequestingGateway -> showLoadingDialog("Requesting gateway...")
                PlayRechargePageState.WaitingForGatewayResponse -> showLoadingDialog("Waiting...")
                is PlayRechargePageState.ReceivedGatewayResponse -> {
                    when (viewModel.paytmResponseModel.STATUS) {
                        "SUCCESS" -> {
                            viewModel.recharge = MobileRechargeModel()
                            viewModel.recharge.UserID = userId
                            viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                            viewModel.recharge.ServiceTypeID = 1
                            viewModel.recharge.WalletTypeID = WalletType.OnlinePayment.id
                            viewModel.recharge.OperatorCode = "11"
                            viewModel.recharge.RechargeAmt =
                                viewModel.rechargeAmount.value!!.toDouble()
                            viewModel.recharge.ServiceField1 = ""
                            viewModel.recharge.ServiceProviderID = 3
                            viewModel.recharge.Status = "Received"
                            viewModel.recharge.TransTypeID = 9
                            viewModel.addUsedServiceDetail(viewModel.recharge)

                        }
                        "FAILURE" -> {
                            viewModel.addPaymentTransactionDetail(
                                PaymentGatewayTransactionModel(
                                    UserId = userId,
                                    OrderId = viewModel.paytmResponseModel.ORDERID,
                                    ReferenceTransactionId = gatewayOrderId,
                                    ServiceTypeId = 1,
                                    WalletTypeId = WalletType.OnlinePayment.id,
                                    TxnAmount = viewModel.paytmResponseModel.TXNAMOUNT,
                                    Currency = viewModel.paytmResponseModel.CURRENCY,
                                    TransactionTypeId = 9,
                                    IsCredit = false,
                                    TxnId = viewModel.paytmResponseModel.TXNID,
                                    Status = viewModel.paytmResponseModel.STATUS,
                                    RespCode = viewModel.paytmResponseModel.RESPCODE,
                                    RespMsg = viewModel.paytmResponseModel.RESPMSG,
                                    BankTxnId = viewModel.paytmResponseModel.BANKTXNID,
                                    BankName = viewModel.paytmResponseModel.GATEWAYNAME,
                                    PaymentMode = viewModel.paytmResponseModel.PAYMENTMODE
                                )
                            )
                        }
                        "CANCELLED" -> {
                            showToast("Transaction Cancelled !!!")
                        }
                        else -> {
                            showActionDialog(this, DialogType.ERROR, "Oops!",
                                "Something went wrong!! Try again later...", mListener = {
                                    finish()
                                })
                        }
                    }
                }

                PlayRechargePageState.SubmittedPaymentTransDetail -> {}
                is PlayRechargePageState.RechargeSuccessful -> {
                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Recharge Successful!!!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        "Your redeem code was successfully generated."
                    )
                    viewModel.recharge.OperatorTransID
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.SUCCESS)
                    bundle.putString(TaskResultDialog.KEY_REF_ID, viewModel.recharge.RequestID)
                    bundle.putString(
                        TaskResultDialog.KEY_ACCOUNT_NUMBER,
                        viewModel.recharge.MobileNo.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_AMOUNT,
                        viewModel.rechargeAmount.value.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        viewModel.rechargeApiResponse.Status
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_REDEEM_CODE,
                        viewModel.rechargeApiResponse.OperatorTransID
                    )
                    bundle.putInt(
                        TaskResultDialog.KEY_WALLET_TYPE_ID,
                        viewModel.recharge.WalletTypeID!!
                    )
                    showTaskResultDialog(bundle, supportFragmentManager)
                }
                is PlayRechargePageState.RechargeFailed -> {
                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Recharge Failed!!!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        "Your redeem code generation was unfortunately failed."
                    )
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.FAILURE)
                    bundle.putString(TaskResultDialog.KEY_REF_ID, viewModel.recharge.RequestID)
                    bundle.putString(
                        TaskResultDialog.KEY_ACCOUNT_NUMBER,
                        viewModel.recharge.MobileNo.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_AMOUNT,
                        viewModel.rechargeAmount.value.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        viewModel.rechargeApiResponse.Status
                    )
                    bundle.putInt(
                        TaskResultDialog.KEY_WALLET_TYPE_ID,
                        viewModel.recharge.WalletTypeID!!
                    )
                    showTaskResultDialog(bundle, supportFragmentManager)
                }
                is PlayRechargePageState.RechargePending -> {
                    val bundle = Bundle()
                    bundle.putString(TaskResultDialog.KEY_HEADING, "Recharge Pending!!!")
                    bundle.putString(
                        TaskResultDialog.KEY_SUBTITLE,
                        "Your redeem code generation is pending."
                    )
                    bundle.putInt(TaskResultDialog.KEY_STATUS, TaskResultDialog.PENDING)
                    bundle.putString(TaskResultDialog.KEY_REF_ID, viewModel.recharge.RequestID)
                    bundle.putString(
                        TaskResultDialog.KEY_ACCOUNT_NUMBER,
                        viewModel.recharge.MobileNo.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_AMOUNT,
                        viewModel.rechargeAmount.value.toString()
                    )
                    bundle.putString(
                        TaskResultDialog.KEY_PAYMENT_STATUS,
                        viewModel.rechargeApiResponse.Status
                    )
                    bundle.putInt(
                        TaskResultDialog.KEY_WALLET_TYPE_ID,
                        viewModel.recharge.WalletTypeID!!
                    )
                    showTaskResultDialog(bundle, supportFragmentManager)
                }
            }
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {

        when (method) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> {
                startPayment()
            }
        }
    }


    private fun startPayment() {
        gatewayOrderId = createRandomOrderId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account = gatewayOrderId,
                amount = viewModel.rechargeAmount.value.toString(),
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                userid = userId
            )
        )

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


    override fun onTransactionResponse(p0: Bundle?) {
        Timber.d("onTransactionResponse : ${p0.toString()}")

        p0?.let {
            viewModel.paytmResponseModel = PaytmResponseModel(
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


            viewModel.pageState.postValue(PlayRechargePageState.ReceivedGatewayResponse(viewModel.paytmResponseModel))

        }

    }

    override fun networkNotAvailable() {

        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {

        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {

        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {

        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {

        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {

        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {

        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }


}