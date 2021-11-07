package com.sampurna.pocketmoney.mlm.ui.mobilerecharge.newui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sampurna.pocketmoney.common.PaymentMethods
import com.sampurna.pocketmoney.databinding.FragmentEnterRechargeAmountBinding
import com.sampurna.pocketmoney.mlm.model.ModelContact
import com.sampurna.pocketmoney.mlm.model.serviceModels.*
import com.sampurna.pocketmoney.mlm.repository.ServiceRepository
import com.sampurna.pocketmoney.mlm.ui.mobilerecharge.MobileNumberDetail
import com.sampurna.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.sampurna.pocketmoney.utils.*
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import androidx.core.widget.doOnTextChanged


@AndroidEntryPoint
class EnterRechargeAmount : BaseFragment<FragmentEnterRechargeAmountBinding>(FragmentEnterRechargeAmountBinding::inflate),
    PaymentMethods.PaymentMethodsInterface, MyCustomToolbar.MyCustomToolbarListener {
    private val viewModel by activityViewModels<MobileRechargeViewModel>()
    private var mCircle: String = "Mumbai"
    private var mOperator: String = "Jio"
    private lateinit var mContact: ModelContact
    private lateinit var userId : String
    private var roleId = 0
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var recharge : MobileRechargeModel
    private var gatewayOrderId : String = ""

    private lateinit var paytmResponseModel: PaytmResponseModel
    @Inject
    lateinit var serviceRepository: ServiceRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK){
                    val operator = data!!.getStringExtra("operator")!!
                    val circle = data.getStringExtra("circle")!!
                    updateOperatorNCircle(operator,circle)

                    viewModel.getMobileSpecialPlanList(mContact.contactNumber!!, mOperator)

                }
                else{
                    showToast("Cancelled !!")
                }

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbarEnterRechargeAmount.setCustomToolbarListener(this)

        binding.btnChangePlan.setOnClickListener {
            val sheet = ChooseRechargePlans()
            sheet.show(parentFragmentManager,sheet.tag)
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.rechargeAmount.postValue(Integer.parseInt(binding.etRechargeAmount.text.toString()))
            val sheet = PaymentMethods(this)
            sheet.show(parentFragmentManager,sheet.tag)
        }

        binding.etRechargeAmount.doOnTextChanged { text, start, before, count ->

            viewModel.rechargeAmount.postValue(text.toString().toInt())
            /*val mgr: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(binding.etRechargeAmount.windowToken, 0)*/
            binding.btnConfirm.isVisible = text.toString().toInt()>10
        }
    }

    private fun updateOperatorNCircle(operator:String,circle:String){
        mOperator = operator
        mCircle = circle
        mContact.circle = mCircle
        mContact.operator = mOperator
        populateUiElements()
    }

    override fun subscribeObservers() {

        viewModel.userId.observe(this,{
            userId = it
        })

        viewModel.userRoleID.observe(this,{
            roleId = it
        })
        viewModel.selectedContact.observe(viewLifecycleOwner, {
            mContact = it

            viewModel.getCircleNOperatorOfMobileNo(mContact.contactNumber!!)
        })
        viewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        it.circle?.let { circle ->
                            mCircle = circle

                        }
                        it.Operator?.let { operator ->
                            mOperator = operator
                        }
                        mContact.circle = mCircle
                        mContact.operator = mOperator


                        populateUiElements()
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
        viewModel.rechargeAmount.observe(viewLifecycleOwner,{
            binding.etRechargeAmount.setText(it.toString())
        })

        viewModel.walletBalance.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.rechargeAmount.value!!){
                            showToast("Insufficient Wallet Balance !!!")

                        }else{
                            recharge = MobileRechargeModel()
                            recharge.UserID = userId
                            recharge.MobileNo = viewModel.selectedContact.value!!.contactNumber
                            recharge.ServiceTypeID = 1
                            recharge.WalletTypeID = 1
                            recharge.OperatorCode = getMobileOperatorCode(mOperator).toString()
                            recharge.RechargeAmt = viewModel.selectedRechargePlan.value!!.rs!!.toDouble()
                            recharge.ServiceField1 = ""
                            recharge.ServiceProviderID = 3
                            recharge.Status = "Received"
                            recharge.TransTypeID = 9
                            viewModel.addUsedServiceDetail(recharge)
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
                        if (it < viewModel.rechargeAmount.value!!){
                            showToast("Insufficient Wallet Balance !!!")


                        }else{
                            recharge = MobileRechargeModel()
                            recharge.UserID = userId
                            recharge.MobileNo = viewModel.selectedContact.value!!.contactNumber
                            recharge.ServiceTypeID = 1
                            recharge.WalletTypeID = 4
                            recharge.OperatorCode = getMobileOperatorCode(mOperator).toString()
                            recharge.RechargeAmt = viewModel.selectedRechargePlan.value!!.rs!!.toDouble()
                            recharge.ServiceField1 = ""
                            recharge.ServiceProviderID = 3
                            recharge.Status = "Received"
                            recharge.TransTypeID = 9
                            viewModel.addUsedServiceDetail(recharge)
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
                        val paytmOrder = PaytmOrder(recharge.RequestID, Constants.P_MERCHANT_ID, it, viewModel.rechargeAmount.value.toString(),
                            Constants.PAYTM_CALLBACK_URL+recharge.RequestID)
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

        viewModel.addUsedServiceDetailResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                       if (it>0){
                           viewModel.getUsedServiceRequestId(userId,recharge.MobileNo!!)
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

        viewModel.usedServiceRequestId.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        recharge.RequestID = it
                        if (serviceRepository.selectedPaymentMethod==PaymentEnum.PAYTM){
                            startPayment()
                        }else{
                            viewModel.callSampurnaRechargeService(recharge)
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



        viewModel.mobileRechargeModel.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        if (serviceRepository.selectedPaymentMethod==PaymentEnum.PAYTM){
                            if (paytmResponseModel.PAYMENTMODE != "UPI"){
                                val amountDeduct = (paytmResponseModel.TXNAMOUNT?.toDouble() ?: 0.0) * 2 / 100

                                viewModel.walletChargeDeduct(userId,2,amountDeduct,recharge.RequestID!!,18)
                            }
                        }else{
                            showToast("Recharge done successfully !!!!")
                            requireActivity().finish()
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

        viewModel.addPaymentTransResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (paytmResponseModel.STATUS == "SUCCESS"){
                            viewModel.callSampurnaRechargeService(recharge)


                        }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                            showToast("Payment failed !!!")

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

        viewModel.walletChargeDeducted.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Recharge done successfully !!!!")
                        requireActivity().finish()
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

    private fun populateUiElements() {
        binding.toolbarEnterRechargeAmount.apply {
            setToolbarLogo(getMobileOperatorLogo(mContact.operator!!))
            if (mContact.contactName.isNullOrEmpty()) {
                setToolbarTitle(mContact.contactNumber!!)
                setToolbarSubtitle("Prepaid - ${mContact.circle}")

            } else {
                setToolbarTitle(mContact.contactName!!)
                setToolbarSubtitle("${mContact.contactNumber} - ${mContact.circle}")
            }
        }
    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        serviceRepository.selectedPaymentMethod = method
        when(method){
            PaymentEnum.WALLET->viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH->viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PAYTM->{
                recharge = MobileRechargeModel()
                recharge.UserID = userId
                recharge.MobileNo = viewModel.selectedContact.value!!.contactNumber
                recharge.ServiceTypeID = 1
                recharge.WalletTypeID = 3
                recharge.OperatorCode = getMobileOperatorCode(mOperator).toString()
                recharge.RechargeAmt = viewModel.selectedRechargePlan.value!!.rs!!.toDouble()
                recharge.ServiceField1 = ""
                recharge.ServiceProviderID = 3
                recharge.Status = "Received"
                recharge.TransTypeID = 9
                viewModel.addUsedServiceDetail(recharge)

            }
        }

    }
    fun startPayment() {
        gatewayOrderId = createRandomAccountId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account= recharge.RequestID,
                amount = viewModel.rechargeAmount.value.toString(),
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                userid = userId
            )
        )

    }

    private fun createRandomAccountId(): String {
        var randomAccountID = "RMS"
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

    override fun onToolbarNavClick() {
        findNavController().navigateUp()
    }

    override fun onMenuClick() {
        val intent = Intent(requireActivity(),MobileNumberDetail::class.java)
        intent.putExtra("operator",mOperator)
        intent.putExtra("circle",mCircle)
        resultLauncher.launch(intent)
    }
    private fun processPaytmTransaction(paytmOrder: PaytmOrder) {
        try {

            val transactionManager =
                TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {


                    override fun onTransactionResponse(p0: Bundle?) {

                        Log.e("PAYTM_TRANS",p0.toString())


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

                        viewModel.addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
//                                ReferenceTransactionId = it,
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
            transactionManager.startTransaction(requireActivity(), 3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}