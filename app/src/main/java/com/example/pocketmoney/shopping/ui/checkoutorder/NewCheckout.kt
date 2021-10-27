package com.example.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityNewCheckoutBinding
import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.ui.TaskResultDialog
import com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui.Recharge
import com.example.pocketmoney.paymentgateway.PaymentPortal
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.PaymentModes
import com.example.pocketmoney.utils.myEnums.PaymentStatus
import com.example.pocketmoney.utils.myEnums.WalletType
import com.jmm.brsap.dialog_builder.DialogType
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCheckout : BaseActivity<ActivityNewCheckoutBinding>(ActivityNewCheckoutBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener,
    PaymentPortal.PaymentPortalCallback, PaytmPaymentTransactionCallback,
    PaymentMethods.PaymentMethodsInterface {

    private val viewModel by viewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository


    private lateinit var userId: String
    private var roleId = 0
    private lateinit var gatewayOrderId: String

    private lateinit var paytmResponseModel: PaytmResponseModel
    private lateinit var orderNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarCheckoutOrder.setApplicationToolbarListener(this)
        binding.btnContinue.setOnClickListener {
            it.isEnabled = false
            val sheet = PaymentMethods(this,true)
            sheet.show(supportFragmentManager, sheet.tag)
            /*val bottomSheet = PaymentPortal(this, viewModel.grandTotal, true)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)*/
        }
    }

    override fun subscribeObservers() {
        viewModel.amountPayable.observe(this, {
            binding.btnContinue.text = "Pay ₹ $it"
        })
        viewModel.userId.observe(this, {
            userId = it
        })

        viewModel.userRoleID.observe(this, {
            roleId = it
        })

        viewModel.progressStatus.observe(this, {
            displayLoading(false)
            hideLoadingDialog()
            when (it) {
                ERROR -> {
                    showActionDialog(this, DialogType.ERROR,"Oops !!!","Something went wrong! Please try again."
                        ,"Okay"
                    ) {
                        // do things here
                    }
                }
                CHECKING_WALLET_BALANCE -> {
                    showLoadingDialog("Checking wallet balance...")
                }
                INSUFFICIENT_BALANCE -> {
                    showActionDialog(this, DialogType.ERROR,"Insufficient balance!!","Your wallet balance is low."
                        ,"Okay"
                    ) {
                        // do things here
                    }
                }
                INITIATING_TRANSACTION ->{
                    showLoadingDialog("Initiating transaction...")
                }
                CHECKSUM_RECEIVED -> {
                    displayLoading(false)
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.P_MERCHANT_ID,
                        viewModel.transactionToken,
                        viewModel.grandTotal.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }

                PROCESSING ->{
                    showLoadingDialog()
                }
            }

        })

        viewModel.progressStatus.observe(this,{
            when(it){
                LOADING->{
                    displayLoading(true)
                }
                SUCCESS->{
                    displayLoading(false)
                }
                ERROR->{
                    displayLoading(false)
                }
                CREATING_ORDER_NUMBER->{
                    displayLoading(false)
                }
                ORDER_SUCCESSFUL->{
                    displayLoading(false)
                }
                MESSAGE_SENT->{
                    displayLoading(false)
                }
                else->{
                    displayLoading(false)
                }
            }
        })
        viewModel.isMessageSent.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val intent = Intent(this, OrderSuccessful::class.java)
                        startActivity(intent)
                        finish()
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

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }


    override fun onPaymentResultReceived(
        method: PaymentEnum,
        result: Boolean,
        message: String,
        paytmResponseModel: PaytmResponseModel?
    ) {
        viewModel.customerOrder = CustomerOrder(
            ShippingAddressId = viewModel.selectedAddressId.value,
            UserID = userId,
            Total = viewModel.grandTotal,
            Discount = viewModel.discountAmount,
            Shipping = viewModel.mShippingCharge,
            Tax = viewModel.tax,
            GrandTotal = viewModel.grandTotal,
            Promo = viewModel.discountCoupon,
        )
        if (result) {
            when (method) {
                PaymentEnum.WALLET -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Paid.id // paid
                    viewModel.customerOrder.WalletTypeId = WalletType.Wallet.id  // wallet
                    viewModel.customerOrder.PaymentMode = PaymentModes.Wallet.id   // wallet
                }
                PaymentEnum.PCASH -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Paid.id // paid
                    viewModel.customerOrder.WalletTypeId = WalletType.PCash.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.PCash.id
                }
                PaymentEnum.PAYTM -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Pending.id
                    viewModel.customerOrder.WalletTypeId = WalletType.OnlinePayment.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.Online.id

                    checkoutRepository.selectedPaymentMethod = PaymentEnum.PAYTM

                    if (paytmResponseModel != null) {
                        viewModel.paytmResponseModel = paytmResponseModel
                    }
                }
                PaymentEnum.COD -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Pending.id
                    viewModel.customerOrder.WalletTypeId = WalletType.CashOnDelivery.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.CashOnDelivery.id
                }
            }
            viewModel.createCustomerOrder(viewModel.customerOrder)
        }
    }



    override fun onPaymentMethodSelected(method: PaymentEnum) {
        when (method) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> { startPayment() }
        }
    }

    private fun startPayment() {
        gatewayOrderId = createRandomOrderId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account = gatewayOrderId,
                amount = viewModel.grandTotal.toString(),
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


            when (viewModel.paytmResponseModel.STATUS) {
                "SUCCESS" -> {
                    viewModel.recharge = MobileRechargeModel()
                    viewModel.recharge.UserID = userId
                    viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                    viewModel.recharge.ServiceTypeID = 1
                    viewModel.recharge.WalletTypeID = WalletType.OnlinePayment.id
                    viewModel.recharge.OperatorCode =
                        getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                    viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                    viewModel.recharge.ServiceField1 = ""
                    viewModel.recharge.ServiceProviderID = 3
                    viewModel.recharge.Status = "Received"
                    viewModel.recharge.TransTypeID = 9
                    viewModel.addUsedServiceDetail(viewModel.recharge)

                }
                "FAILURE" -> {
//                    showToast("Transaction Failed !!!")
                    viewModel.addPaymentTransactionDetail(
                        PaymentGatewayTransactionModel(
                            UserId = userId,
                            OrderId = viewModel.paytmResponseModel.ORDERID,
                            ReferenceTransactionId = gatewayOrderId,
                            ServiceTypeId = 1,
                            WalletTypeId = WalletType.OnlinePayment.id,
                            TxnAmount = viewModel.paytmResponseModel.TXNAMOUNT,
                            Currency = viewModel.paytmResponseModel.CURRENCY,
                            TransactionTypeId = 1,
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
//                    showToast("Something went wrong !!!")
                    showActionDialog(this, DialogType.ERROR,"Oops!",
                        "Something went wrong!! Contact admin...",mListener = {
                            finish()
                        })
                }
            }

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


    companion object{
        const val LOADING = 100
        const val SUCCESS = 101
        const val ERROR = 102
        const val CREATING_ORDER_NUMBER = 103
        const val ORDER_SUCCESSFUL = 104
        const val MESSAGE_SENT = 105
        const val CHECKING_WALLET_BALANCE = 106
        const val INSUFFICIENT_BALANCE = 107
        const val CHECKSUM_RECEIVED = 108
        const val START_PAYMENT_GATEWAY = 109
        const val PROCESSING = 110
        const val INITIATING_TRANSACTION = 111
        const val ADDING_PAYMENT_TRANS_DETAIL = 112

    }



}