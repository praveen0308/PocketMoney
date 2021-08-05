package com.example.pocketmoney.shopping.ui.checkoutorder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityCheckoutOrderBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.Constants.P_MERCHANT_ID
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import java.util.*


@AndroidEntryPoint
class CheckoutOrder : BaseActivity<ActivityCheckoutOrderBinding>(ActivityCheckoutOrderBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    //UI
    private lateinit var dialog: AlertDialog

    // ViewModels
    private val viewModel by viewModels<CheckoutOrderViewModel>()

    // Adapters

    // Variable
    private var selectedAddressId: Int = 0

    // Navigation
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var navHostFragment: NavHostFragment


    private lateinit var ORDER_ID: String
    private lateinit var ACCOUNT_ID : String
    private lateinit var AMOUNT : String
    private lateinit var userId : String
    var bodyData = ""
    var value = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(this,R.id.nav_host_checkout_order)
        binding.toolbarCheckoutOrder.setApplicationToolbarListener(this)
        createProgressDialog()
//        setupStartDestination(0)
        setupStepView()
        binding.btnDeliverHere.setOnClickListener {
            navController.navigate(
                R.id.action_selectAddress_to_orderSummary,
                OrderSummaryArgs(selectedAddressId).toBundle()
            )
        }
    }

    fun setupStartDestination(step: Int) {
//        navHostFragment = nav_host_checkout_order as NavHostFragment
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

        binding.btnContinue.setOnClickListener {
            when (binding.stepView.currentStep) {
                1 -> navController.navigate(
                        R.id.action_orderSummary_to_payment,
                        PaymentArgs(selectedAddressId).toBundle()
                )
                2 -> {
//                    val fragment = navHostFragment.childFragmentManager.fragments[0] as Payment
//                    viewModel.createCustomerOrder(fragment.getCustomerOrder())
                    it.isEnabled = false
                    startPayment()

                }

                else -> {
                    //Nothing
                }
            }
        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })
        viewModel.activeStep.observe(this,{
            binding.apply {
                stepView.go(it,true)
                when(it){
                    0->{
                        btnDeliverHere.isVisible=true
                        layoutCheckoutAction.isVisible=false
                    }
                    1->{
                        btnDeliverHere.isVisible=false
                    }
                    2->{
                        btnDeliverHere.isVisible=false
                    }
                }
            }
        })

        viewModel.amountPayable.observe(this,{
            AMOUNT = it.toString()
            binding.layoutCheckoutAction.isVisible = true
            binding.tvAmountPayable.setAmount(it)
            binding.btnContinue.text = getString(R.string.str_continue)

        })

        viewModel.selectedAddress.observe(this,{
            it?.let {
                selectedAddressId = it.AddressID!!
                binding.btnDeliverHere.isVisible=true
            }
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

                        val paytmOrder = PaytmOrder(
                            ACCOUNT_ID,
                            P_MERCHANT_ID,
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
        ACCOUNT_ID = createRandomAccountId()
        ORDER_ID = createRandomOrderId()
        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account= ACCOUNT_ID,
                amount = AMOUNT,
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                userid = userId
            )
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode && data != null) {
            val nsdk = data.getStringExtra("nativeSdkForMerchantMessage")
            val response = data.getStringExtra("response")
            Toast.makeText(this, nsdk + response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {

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
            transactionManager.setAppInvokeEnabled(false)
            transactionManager.startTransaction(this, 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun createRandomAccountId(): String {
        var randomAccountID = "DI"
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

    private fun createRandomOrderId(): String {
        val timeSeed = System.nanoTime()
        val randSeed = Math.random() * 1000
        val midSeed = (timeSeed * randSeed).toLong()
        val s = midSeed.toString() + ""
        return s.substring(0, 9)
    }
}