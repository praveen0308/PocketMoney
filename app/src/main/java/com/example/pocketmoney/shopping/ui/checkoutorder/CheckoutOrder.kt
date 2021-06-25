package com.example.pocketmoney.shopping.ui.checkoutorder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.loader.app.LoaderManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityCheckoutOrderBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.shopping.adapters.CartItemListAdapter
import com.example.pocketmoney.shopping.ui.CheckoutOrderInterface
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import io.github.parthav46.httprequest.HttpRequest
import kotlinx.android.synthetic.main.activity_checkout_order.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class CheckoutOrder : BaseActivity<ActivityCheckoutOrderBinding>(ActivityCheckoutOrderBinding::inflate), CheckoutOrderInterface,
    ApplicationToolbar.ApplicationToolbarListener {

    //UI
    private lateinit var dialog: AlertDialog

    // ViewModels
    private val viewModel by viewModels<CheckoutOrderViewModel>()

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter

    // Variable
    private var selectedAddressId: Int = 0

    // Navigation

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var navHostFragment: NavHostFragment


    var ORDER_ID: String? = null
    var loaderManager: LoaderManager? = null
    var bodyData = ""
    var value = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarCheckoutOrder.setApplicationToolbarListener(this)
        createProgressDialog()
        setupStartDestination(0)
        setupStepView()

    }

    fun setupStartDestination(step: Int) {
        navHostFragment = nav_host_checkout_order as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_checkout_order)
        navController = navHostFragment.navController
        navController.setGraph(navGraph, AddressArgs(ShoppingEnum.CHECKOUT).toBundle())
    }

    private fun setupStepView() {
        binding.stepView.state
            .steps(object : ArrayList<String?>() {
                init {
                    add("Address")
                    add("Order Summary")
                    add("Payment")
                }
            })
            .stepsNumber(3)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime)) // other state methods are equal to the corresponding xml attributes
            .commit()

        binding.layoutCheckoutAction.btnContinue.setOnClickListener {
            when (stepView.currentStep) {
                1 -> navController.navigate(
                        R.id.action_orderSummary_to_payment,
                        PaymentArgs(selectedAddressId).toBundle()
                )
                2 -> {
                    val fragment = navHostFragment.childFragmentManager.fragments[0] as Payment
//                    viewModel.createCustomerOrder(fragment.getCustomerOrder())
                    startPayment()

                }

                else -> {
                    //Nothing
                }
            }
        }

    }

    override fun subscribeObservers() {
        viewModel.activeStep.observe(this,{
            binding.stepView.go(it,true)
        })

        viewModel.orderStatus.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            navController.navigate(R.id.action_payment_to_orderSuccessful)
                            displayError("Order Successfully !!!")
                            finish()
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

        viewModel.checkSum.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val paytmParams = JSONObject()
                        val head = JSONObject()
                        val checksum = it
                        Log.e("checksum", checksum)
                        head.put("signature", checksum)
                        paytmParams.put("head", head)
                        paytmParams.put("body", JSONObject(bodyData))
                        val url =
                            "https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=" + Constants.MERCHANT_ID.toString() + "&orderId=" + ORDER_ID

                        HttpRequest(
                            this@CheckoutOrder,
                            url,
                            HttpRequest.Request.POST,
                            paytmParams.toString()
                        ) { response ->
                            if (response != null) {
                                try {
                                    processPaytmTransaction(JSONObject(response))
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                } finally {
                                    ORDER_ID = "ID" + System.currentTimeMillis()

                                }
                            }

                        }.execute()
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


    override fun onDeliveryAddressSelected(addressId: Int) {
        navController.navigate(
                R.id.action_address_to_orderSummary,
                OrderSummaryArgs(addressId).toBundle()
        )
        selectedAddressId = addressId
        stepView.go(1, true)
    }

    override fun updateCheckOutStepStatus(step: Int) {
        stepView.go(step, true)

    }

    override fun setPriceDetailNAction(amountPayable: Double) {
        binding.layoutCheckoutAction.tvAmountPayable.text = "₹ ".plus(amountPayable.toString())
        binding.layoutCheckoutAction.btnContinue.text = getString(R.string.str_continue)
        binding.layoutCheckoutAction.root.visibility = View.VISIBLE

    }

    override fun onToolbarNavClick() {
        navController.popBackStack()
    }

    override fun onMenuClick() {
    }

    private fun createProgressDialog(){
        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Processing Order...")
                .setCancelable(false)
                .setTheme(R.style.CustomProgressDialog)
                .build()
    }


    fun startPayment() {

        bodyData = getPaytmParams()
        viewModel.initiateTransactionApi(
            PaytmRequestData(
                amount = "150",
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                orderid = "5485485748",
                userid = "8767404060"
            )
        )
//        HttpRequest(
//            this,
//            Constants.CHECKSUM,
//            HttpRequest.Request.POST,
//            bodyData
//        ) { response ->
//            if (response != null) {
//                try {
//                    val paytmParams = JSONObject()
//                    val head = JSONObject()
//                    val checksum = JSONObject(response).getString("checksum")
//                    Log.e("checksum", checksum)
//                    head.put("signature", checksum)
//                    paytmParams.put("head", head)
//                    paytmParams.put("body", JSONObject(bodyData))
//                    val url =
//                        "https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=" + Constants.MERCHANT_ID.toString() + "&orderId=" + ORDER_ID
//                    HttpRequest(
//                        this@CheckoutOrder,
//                        url,
//                        HttpRequest.Request.POST,
//                        paytmParams.toString()
//                    ) { response ->
//                        if (response != null) {
//                            try {
//                                processPaytmTransaction(JSONObject(response))
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                            } finally {
//                                ORDER_ID = "ID" + System.currentTimeMillis()
//                                orderID.setText(ORDER_ID)
//                            }
//                        }
//                        if (progressDialog.isShowing) progressDialog.dismiss()
//                    }.execute()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    if (progressDialog.isShowing) progressDialog.dismiss()
//                }
//            } else {
//                if (progressDialog.isShowing) progressDialog.dismiss()
//            }
//        }.execute()
    }

    fun getPaytmParams(): String {
        var paytmParams: JSONObject
        try {
            val body = JSONObject()
            body.put("requestType", "Payment")
            body.put("mid", Constants.MERCHANT_ID)
            body.put("websiteName", Constants.WEBSITE)
            body.put("orderId", ORDER_ID)
            body.put("callbackUrl", Constants.PAYTM_CALLBACK_URL)
            val txnAmount = JSONObject()
            try {
                value = binding.layoutCheckoutAction.tvAmountPayable.text.toString().toFloat()
            } catch (e: Exception) {
                value = 0f
            }
            txnAmount.put("value", String.format(Locale.getDefault(), "%.2f", value))
            txnAmount.put("currency", "INR")
            val userInfo = JSONObject()
            userInfo.put("custId", "CUST_001")
            body.put("txnAmount", txnAmount)
            body.put("userInfo", userInfo)

            /*
             * Generate checksum by parameters we have in body
             * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
             * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
             */paytmParams = body
        } catch (e: Exception) {
            e.printStackTrace()
            paytmParams = JSONObject()
        }
        return paytmParams.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode && data != null) {
            val nsdk = data.getStringExtra("nativeSdkForMerchantMessage")
            val response = data.getStringExtra("response")
            Toast.makeText(this, nsdk + response, Toast.LENGTH_SHORT).show()
        }
    }

    fun processPaytmTransaction(data: JSONObject) {
        try {
            Log.i("CHECKSUM", data.getJSONObject("body").toString())
            Log.i("CHECKSUM", data.getJSONObject("head").getString("signature"))
            Log.e("TXN_TOKEN", data.getJSONObject("body").getString("txnToken"))
            val paytmOrder = PaytmOrder(
                ORDER_ID,
                Constants.MERCHANT_ID,
                data.getJSONObject("body").getString("txnToken"),
                String.format(
                    Locale.getDefault(), "%.2f", value
                ),
                Constants.PAYTM_CALLBACK_URL
            )
            val transactionManager =
                TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {


                    override fun onTransactionResponse(p0: Bundle?) {
                        Toast.makeText(
                            applicationContext,
                            "Payment Transaction response $p0", Toast.LENGTH_LONG
                        ).show()
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
            transactionManager.startTransaction(this, 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}