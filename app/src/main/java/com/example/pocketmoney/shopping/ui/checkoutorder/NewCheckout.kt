package com.example.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityNewCheckoutBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.PaymentModes
import com.example.pocketmoney.utils.myEnums.PaymentStatus
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCheckout : BaseActivity<ActivityNewCheckoutBinding>(ActivityNewCheckoutBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, PaymentMethods.PaymentMethodsInterface {

    private val viewModel by viewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository:CheckoutRepository


    private lateinit var userId : String
    private var roleId = 0
    private lateinit var gatewayOrderId : String

    private lateinit var paytmResponseModel: PaytmResponseModel
    private lateinit var orderNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarCheckoutOrder.setApplicationToolbarListener(this)
        binding.btnContinue.setOnClickListener {
            it.isEnabled = false
            val bottomSheet = PaymentMethods(this,true)
            bottomSheet.show(supportFragmentManager,bottomSheet.tag)
        }
    }

    override fun subscribeObservers() {
        viewModel.amountPayable.observe(this,{
            binding.btnContinue.text = "Pay ₹ $it"
        })
        viewModel.userId.observe(this,{
            userId = it
        })

        viewModel.userRoleID.observe(this,{
            roleId = it
        })
        checkoutRepository.appliedCouponCode.observe(this,{
            if (!it.isNullOrEmpty()){

            }
        })

        viewModel.isValidCoupon.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

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

        viewModel.couponDetail.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.IsFixed){

                        }else{

                        }
//
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
        viewModel.checkSum.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        val paytmOrder = PaytmOrder(gatewayOrderId, Constants.P_MERCHANT_ID, it, viewModel.grandTotal.toString(),
                            Constants.PAYTM_CALLBACK_URL+gatewayOrderId)
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

        viewModel.orderNumber.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it !=null) {
                            orderNumber = it
                            when(checkoutRepository.selectedPaymentMethod){
                                PaymentEnum.PAYTM->{
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
                                else->{
                                    val message1 =
                                        "Thank you for shopping with pocketmoney, your order placed successfully. Your order number is  $orderNumber you can track your order using pocketmoney, click https//wwww.pocketmoney.net.in"
                                    viewModel.sendWhatsappMessage(userId,message1)
                                    displayError("Order Successfully !!!")
                                }
                            }

                        } else {
                            displayError("Something went wrong. Try Again !!!")
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

      /*  viewModel.checkSum.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        val paytmOrder = PaytmOrder(gatewayOrderId,
                            Constants.P_MERCHANT_ID, it, viewModel.grandTotal.toString(),
                            Constants.PAYTM_CALLBACK_URL+gatewayOrderId)
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
        })*/

        viewModel.addPaymentTransResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (paytmResponseModel.STATUS == "SUCCESS"){
                            viewModel.updatePaymentStatus(orderNumber,PaymentStatus.Paid.id)
                            val message1: String =
                                "Thank you for shopping with pocketmoney, your order placed successfully. Your order number is  " + orderNumber.toString() + " you can track your order using pocketmoney, click https//wwww.pocketmoney.net.in"
                            viewModel.sendWhatsappMessage(userId,message1)
                        }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){

                            val message = "Thank you for shopping with pocketmoney, your order payment has been failed. please revisit pocketmoney to place order again, click https//wwww.pocketmoney.net.in"
                            viewModel.updatePaymentStatus(orderNumber,PaymentStatus.Failed.id)
                            viewModel.sendWhatsappMessage(userId,message)
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

        viewModel.isPaymentStatusUpdated.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (paytmResponseModel.STATUS == "SUCCESS"){
                            val message1: String =
                                "Thank you for shopping with pocketmoney, your order placed successfully. Your order number is  " + orderNumber.toString() + " you can track your order using pocketmoney, click https//wwww.pocketmoney.net.in"
                            viewModel.sendWhatsappMessage(userId,message1)
                        }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                            val message = "Thank you for shopping with pocketmoney, your order payment has been failed. please revisit pocketmoney to place order again, click https//wwww.pocketmoney.net.in"
                            viewModel.sendWhatsappMessage(userId,message)
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

        viewModel.walletBalance.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.grandTotal){
                            showToast("Insufficient Wallet Balance !!!")
                            binding.btnContinue.isEnabled = true

                        }
                        else{
                            val order = CustomerOrder(
                                ShippingAddressId=viewModel.selectedAddressId.value,
                                UserID = userId,
                                Total = viewModel.grandTotal,
                                Discount = 0.0,
                                Shipping = viewModel.mShippingCharge,
                                Tax = viewModel.tax,
                                GrandTotal = viewModel.grandTotal,
                                Promo = viewModel.discountCoupon,
                                PaymentStatusId = PaymentStatus.Paid.id, // paid
                                WalletTypeId = 1,  // wallet
                                PaymentMode = PaymentModes.Wallet.id,   // wallet

                            )

                            viewModel.createCustomerOrder(order)
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
        viewModel.pCash.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.grandTotal){
                            showToast("Insufficient PCash Balance !!!")
                            binding.btnContinue.isEnabled = true
                        } else{
                            val order = CustomerOrder(
                                ShippingAddressId=viewModel.selectedAddressId.value,
                                UserID = userId,
                                Total = viewModel.grandTotal,
                                Discount = 0.0,
                                Shipping = viewModel.mShippingCharge,
                                Tax = viewModel.tax,
                                GrandTotal = viewModel.grandTotal,
                                Promo = viewModel.discountCoupon,
                                PaymentStatusId = PaymentStatus.Paid.id, // paid
                                WalletTypeId = 2,  // wallet
                                PaymentMode = PaymentModes.PCash.id,   // wallet
                            )

                            viewModel.createCustomerOrder(order)
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
        viewModel.isMessageSent.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val intent = Intent(this,OrderSuccessful::class.java)
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

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        checkoutRepository.selectedPaymentMethod = method
        when(method){
            PaymentEnum.WALLET->{

                // check if wallet balance > payment amount
                viewModel.getWalletBalance(userId,roleId)
            }
            PaymentEnum.PCASH->{
                // check if wallet balance > payment amount
                viewModel.getPCashBalance(userId,roleId)
            }
            PaymentEnum.PAYTM->{
                // show some message like redirecting to payment gateway

                startPayment()
            }
            PaymentEnum.COD->{
                val order = CustomerOrder(
                    ShippingAddressId=viewModel.selectedAddressId.value,
                    UserID = userId,
                    Total = viewModel.grandTotal,
                    Discount = checkoutRepository.appliedDiscount,
                    Shipping = viewModel.mShippingCharge,
                    Tax = viewModel.tax,
                    GrandTotal = viewModel.grandTotal,
                    Promo = viewModel.discountCoupon,
                    PaymentStatusId = PaymentStatus.Pending.id,
                    WalletTypeId = 4,  // COD
                    PaymentMode = PaymentModes.CashOnDelivery.id,
                )

                viewModel.createCustomerOrder(order)
            }
        }
    }
    fun startPayment() {
        gatewayOrderId = createRandomAccountId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account= gatewayOrderId,
                amount = viewModel.grandTotal.toString(),
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                userid = userId
            )
        )

    }

    private fun createRandomAccountId(): String {
        var randomAccountID = "PM"
        val ranChar = 65 + Random().nextInt(90 - 65)
        val ch = ranChar.toChar()
        randomAccountID += ch
        val r = Random()
        val numbers = 100000 + (r.nextFloat() * 899900).toInt()
        randomAccountID += numbers.toString()
        randomAccountID += "-"
        var i = 0
        while (i < 6) {
            val ranAny = 48 + Random().nextInt(90 - 65)
            if (ranAny !in 58..65) {
                val c = ranAny.toChar()
                randomAccountID += c
                i++
            }
        }
        return randomAccountID
    }

    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {

            val transactionManager =
                TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {


                    override fun onTransactionResponse(p0: Bundle?) {

                        Log.e("PAYTM_TRANS",p0.toString())

                        val order = CustomerOrder(
                            ShippingAddressId=viewModel.selectedAddressId.value,
                            UserID = userId,
                            Total = viewModel.grandTotal,
                            Discount = 0.0,
                            Shipping = viewModel.mShippingCharge,
                            Tax = viewModel.tax,
                            GrandTotal = viewModel.grandTotal,
                            Promo = viewModel.discountCoupon,
                            PaymentStatusId = PaymentStatus.Pending.id, // paid
                            WalletTypeId = 3,  // wallet
                            PaymentMode = PaymentModes.Online.id,   // wallet
                        )
                        checkoutRepository.selectedPaymentMethod = PaymentEnum.PAYTM
                        viewModel.createCustomerOrder(order)

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



                    }

                    override fun networkNotAvailable() {
                        Log.e("RESPONSE", "network not available")
                    }

                    override fun onErrorProceed(s: String) {
                        Log.e("RESPONSE", "error proceed: $s")
                    }

                    override fun clientAuthenticationFailed(s: String) {
                        Log.e("RESPONSE", "client auth failed: $s")
                    }

                    override fun someUIErrorOccurred(s: String) {
                        Log.e("RESPONSE", "UI error occured: $s")
                    }

                    override fun onErrorLoadingWebPage(i: Int, s: String, s1: String) {
                        Log.e("RESPONSE", "error loading webpage: $s--$s1")
                    }

                    override fun onBackPressedCancelTransaction() {
                        Log.e("RESPONSE", "back pressed")
                    }

                    override fun onTransactionCancel(s: String, bundle: Bundle?) {
                        Log.e("RESPONSE", "transaction cancel: $s")
                    }
                })
            transactionManager.setAppInvokeEnabled(false)
            transactionManager.startTransaction(this, 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}