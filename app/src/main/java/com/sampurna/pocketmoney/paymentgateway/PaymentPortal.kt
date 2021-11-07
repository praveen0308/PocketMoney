package com.sampurna.pocketmoney.paymentgateway

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentPaymentPortalBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.shopping.adapters.PaymentMethodAdapter
import com.sampurna.pocketmoney.shopping.model.ModelPaymentMethod
import com.sampurna.pocketmoney.utils.*
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PaymentPortal(private val paymentPortalCallback: PaymentPortalCallback,
                    private val mAmount:Double, private val isCod:Boolean = false)
    : BaseBottomSheetDialogFragment<FragmentPaymentPortalBinding>(FragmentPaymentPortalBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface, PaytmPaymentTransactionCallback {

    private val viewModel by viewModels<PaymentPortalViewModel>()
    private var selectedPaymentMethod = PaymentEnum.WALLET

    private var userID: String = ""
    private var roleID: Int = 0

    private lateinit var gatewayOrderId : String

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvPaymentMethods()

        binding.btnMakePayment.setButtonClick {
            when(selectedPaymentMethod){
                PaymentEnum.COD->{
                    paymentPortalCallback.onPaymentResultReceived(PaymentEnum.COD,true,"Valid Balance")
                }
                PaymentEnum.WALLET->viewModel.getWalletBalance(userID, roleID)
                PaymentEnum.PCASH->viewModel.getPCashBalance(userID, roleID)
                PaymentEnum.PAYTM->{
                    gatewayOrderId = createRandomOrderId()

                    viewModel.initiateTransactionApi(
                        PaytmRequestData(
                            account= gatewayOrderId,
                            amount = mAmount.toString(),
                            callbackurl = Constants.PAYTM_CALLBACK_URL,
                            userid = userID
                        )
                    )
                }
            }
        }

    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter = PaymentMethodAdapter(getPaymentMethods(),this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }

    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.WALLET, "Wallet", R.drawable.ic_logo,true))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PAYTM, "Payment gateway", R.drawable.ic_paytm_logo))

        if (isCod) paymentMethods.add(ModelPaymentMethod(PaymentEnum.COD, "Cash On Delivery", R.drawable.ic_paytm_logo))
        return paymentMethods
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it
        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
        })
        viewModel.walletBalance.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
                        if (it >= mAmount){
                            paymentPortalCallback.onPaymentResultReceived(PaymentEnum.WALLET,true,"Valid Balance")
                            dismiss()
                        }
                        else {
//                        //    paymentPortalCallback.onPaymentResultReceived(PaymentEnum.WALLET,false,"Insufficient Balance !!!")
                            showToast("Insufficient balance !!!")
                        }

                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
                        displayError(it)
                    }
                }
            }
        })
        viewModel.pCash.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
                        if (it >= mAmount){
                            paymentPortalCallback.onPaymentResultReceived(PaymentEnum.PCASH,true,"Valid Balance")
                            dismiss()
                        }
                        else {
                            showToast("Insufficient balance !!!")
                        }
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
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
                            gatewayOrderId,
                            Constants.P_MERCHANT_ID,
                            it,
                            mAmount.toString(),
                            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$gatewayOrderId"
                        )
                        processPaytmTransaction(paytmOrder)
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    respondButton(LoadingButton.LoadingStates.LOADING,msg = "Processing...")
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
                        displayError(it)
                    }
                }
            }
        })

    }

    private fun respondButton(state: LoadingButton.LoadingStates, mText:String="", msg:String=""){
        binding.btnMakePayment.setState(state, mText, msg)
    }
    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {
            val transactionManager =
                TransactionManager(paytmOrder, this)
            transactionManager.setAppInvokeEnabled(false)
            transactionManager.startTransaction(requireActivity(), 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onPaymentModeSelected(item: ModelPaymentMethod) {
        selectedPaymentMethod = item.method
    }

    override fun onTransactionResponse(p0: Bundle?) {
        Timber.d("onTransactionResponse : ${p0.toString()}")
        respondButton(LoadingButton.LoadingStates.NORMAL,"Make Payment")
        p0?.let {
            val paytmResponseModel = PaytmResponseModel(
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
            when(paytmResponseModel.STATUS){
                "SUCCESS"->{
                    paymentPortalCallback.onPaymentResultReceived(PaymentEnum.PAYTM,true,"Transaction Successful",paytmResponseModel)
                    dismiss()
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

    interface PaymentPortalCallback{
        fun onPaymentResultReceived(method: PaymentEnum,result:Boolean,message:String,paytmResponseModel: PaytmResponseModel?=null)
    }
}