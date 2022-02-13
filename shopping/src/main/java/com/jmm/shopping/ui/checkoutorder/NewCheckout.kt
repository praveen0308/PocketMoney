package com.jmm.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.core.utils.Constants
import com.jmm.core.utils.createRandomOrderId
import com.jmm.core.utils.showActionDialog
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.PaymentModes
import com.jmm.model.myEnums.PaymentStatus
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.shopping_models.CustomerOrder
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.shopping.databinding.ActivityNewCheckoutBinding
import com.jmm.shopping.viewmodel.CheckoutOrderViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NewCheckout : BaseActivity<ActivityNewCheckoutBinding>(ActivityNewCheckoutBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener,
     PaytmPaymentTransactionCallback,
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
            val sheet = PaymentMethods(this,true)
            sheet.show(supportFragmentManager, sheet.tag)
            /*val bottomSheet = PaymentPortal(this, viewModel.grandTotal, true)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)*/
        }
    }

    override fun subscribeObservers() {
        viewModel.amountPayable.observe(this, {
            binding.tvAmount.setText("₹$it")

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
                    binding.btnContinue.isEnabled = true
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
                CREATING_ORDER_NUMBER ->{
                    showLoadingDialog("Please wait!! Creating Order...")
                }
                CHECKSUM_RECEIVED -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.P_MERCHANT_ID,
                        viewModel.transactionToken,
                        viewModel.grandTotal.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }
                MESSAGE_SENT ->{
                    val intent = Intent(this, OrderSuccessful::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                PROCESSING ->{
                    showLoadingDialog()
                }
                else->{
                    showLoadingDialog()
                }
            }

        })


    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

/*

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
*/



    override fun onPaymentMethodSelected(method: PaymentEnum) {
        when (method) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> {
                startPayment()
                checkoutRepository.selectedPaymentMethod = PaymentEnum.PAYTM
            }
            PaymentEnum.COD->{
                viewModel.customerOrder = CustomerOrder(
                    ShippingAddressId = viewModel.selectedAddressId.value,
                    UserID = userId,
                    Total = viewModel.grandTotal,
                    Discount = viewModel.discountAmount,
                    Shipping = viewModel.mShippingCharge,
                    Tax = viewModel.tax,
                    GrandTotal = viewModel.grandTotal,
                    Promo = viewModel.discountCoupon,
                    PaymentStatusId = PaymentStatus.Pending.id,
                    WalletTypeId = WalletType.CashOnDelivery.id,
                    PaymentMode = PaymentModes.CashOnDelivery.id
                )

                viewModel.createCustomerOrder(viewModel.customerOrder)
            }
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
                    viewModel.customerOrder = CustomerOrder(
                        ShippingAddressId = viewModel.selectedAddressId.value,
                        UserID = userId,
                        Total = viewModel.grandTotal,
                        Discount = viewModel.discountAmount,
                        Shipping = viewModel.mShippingCharge,
                        Tax = viewModel.tax,
                        GrandTotal = viewModel.grandTotal,
                        Promo = viewModel.discountCoupon,
                        PaymentStatusId = PaymentStatus.Pending.id,
                        WalletTypeId = WalletType.OnlinePayment.id,
                        PaymentMode = PaymentModes.Online.id
                    )

                    viewModel.createCustomerOrder(viewModel.customerOrder)
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
        const val UPDATING_PAYMENT_STATUS = 113
        const val SENDING_WHATSAPP_MESSAGE = 114


    }



}