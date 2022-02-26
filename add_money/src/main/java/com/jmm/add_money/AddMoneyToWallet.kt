package com.jmm.add_money


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.jmm.add_money.databinding.ActivityAddMoneyToWalletBinding
import com.jmm.core.utils.Constants
import com.jmm.core.utils.createRandomOrderId
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.LoadingButton
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber


@AndroidEntryPoint
class AddMoneyToWallet :
    BaseActivity<ActivityAddMoneyToWalletBinding>(ActivityAddMoneyToWalletBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, PaytmPaymentTransactionCallback {

    private lateinit var gatewayOrderId: String
    private lateinit var mAmount: String
    private var userId = ""

    private val viewModel by viewModels<AddMoneyToWalletViewModel>()

    private lateinit var paytmResponseModel: PaytmResponseModel

    private var requestId: String = ""

    private val mRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarAddMoneyToWallet.setApplicationToolbarListener(this)

        binding.etAmount.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                if (it.toString().toInt() != 0) {
                    binding.btnPay.isVisible = true
                }
            } else {
                binding.btnPay.isVisible = false
            }
        }


        binding.btnPay.setButtonClick {
            if (userId.isEmpty()) {
//                checkAuthorization()
            } else {
                gatewayOrderId = createRandomOrderId()
                mAmount = binding.etAmount.text.toString()
                viewModel.initiateTransactionApi(
                    PaytmRequestData(
                        account = gatewayOrderId,
                        amount = mAmount,
                        callbackurl = Constants.PAYTM_CALLBACK_URL,
                        userid = userId
                    )
                )
            }

        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userId = it
            if (userId.isEmpty()) {
                checkAuthorization()
            }
        }

        viewModel.pageState.observe(this){state->

            when(state){
                AddMoneyToWalletPageState.Initial -> {}
                AddMoneyToWalletPageState.Loading -> respondButton(LoadingButton.LoadingStates.LOADING, msg = "Processing..")
                is AddMoneyToWalletPageState.Error ->{
                    respondButton(LoadingButton.LoadingStates.RETRY, "Retry")
                    showToast(state.msg)
                }
                AddMoneyToWalletPageState.CancelledGateway -> {
                    respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
                    showToast("Cancelled!!!")
                }

                AddMoneyToWalletPageState.Failed -> {
                    respondButton(LoadingButton.LoadingStates.RETRY, "Retry")
                    showToast("Failed!!!")
                }


                is AddMoneyToWalletPageState.ReceivedChecksum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.MERCHANT_ID,
                        state.checksum,
                        mAmount,
                        Constants.PAYTM_CALLBACK_URL+gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }
                is AddMoneyToWalletPageState.ReceivedGatewayResponse -> {
                    when (state.paytmResponseModel.STATUS) {
                        "SUCCESS" -> {
                            state.paytmResponseModel.MID = userId
                            viewModel.addMoneyToWallet(state.paytmResponseModel)
                        }
                        "FAILURE","FAILED" -> {
                            showToast("Transaction Failed !!!")
                            respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
                        }
                        "CANCELLED" -> {
                            showToast("Transaction Cancelled !!!")
                            respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
                        }
                        else -> {
                            showToast("Something went wrong !!!")
                            respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
                        }
                    }
                }
                AddMoneyToWalletPageState.RequestingGateway -> TODO()
                AddMoneyToWalletPageState.Success -> {
                    binding.etAmount.setText("")
                    binding.btnPay.isVisible = false
                    showToast("Money added successfully!!!")
                }
            }

        }



    }

    private fun respondButton(
        state: LoadingButton.LoadingStates,
        mText: String = "",
        msg: String = ""
    ) {
        binding.btnPay.setState(state, mText, msg)
    }

    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {
            val transactionManager =
                TransactionManager(paytmOrder, this)
            transactionManager.setAppInvokeEnabled(true)
//            transactionManager.startTransaction(this, 3)
            transactionManager.startTransactionAfterCheckingLoginStatus(this,  Constants.MERCHANT_ID, mRequestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mRequestCode && data != null) {
//            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
            if (data.getStringExtra("response").isNullOrEmpty()){
                showToast("Transaction Cancelled !!!")
                respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")

            }else{

                val json = JSONObject(data.getStringExtra("response")!!)
                Timber.d("response from str: $json")

                viewModel.pageState.postValue(AddMoneyToWalletPageState.ReceivedGatewayResponse(PaymentMethods.getPaytmResponse(json)))

            }
        }else{
            showToast("Transaction Cancelled !!!")
            respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
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
            viewModel.pageState.postValue(AddMoneyToWalletPageState.ReceivedGatewayResponse(PaymentMethods.getPaytmResponse(it)))

        }



    }

    override fun networkNotAvailable() {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        respondButton(LoadingButton.LoadingStates.NORMAL, "Make Payment")
        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }

}