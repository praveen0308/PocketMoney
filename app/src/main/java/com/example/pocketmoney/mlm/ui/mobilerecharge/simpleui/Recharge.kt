package com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.FragmentRechargeBinding
import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.repository.ServiceRepository
import com.example.pocketmoney.mlm.ui.mobilerecharge.SelectContact
import com.example.pocketmoney.mlm.ui.mobilerecharge.newui.ChooseRechargePlans
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.PlanType
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Recharge(val mListener : MobileRechargeInterface) : BaseFragment<FragmentRechargeBinding>(FragmentRechargeBinding::inflate),
    PaymentMethods.PaymentMethodsInterface {

    private val viewModel by activityViewModels<MobileRechargeViewModel>()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var mCircle: String = "Mumbai"
    private var mOperator: String = "Jio"
    @Inject
    lateinit var serviceRepository: ServiceRepository
    private lateinit var userId : String
    private var roleId = 0

    private lateinit var recharge : MobileRechargeModel
    private var gatewayOrderId : String = ""

    private lateinit var paytmResponseModel: PaytmResponseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK){
                    val number = data!!.getStringExtra("Number")!!
                    binding.etMobileNumber.setText(number.toString())
                }
                else{
                    showToast("Cancelled !!")
                }

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContacts.setOnClickListener {
            val intent = Intent(requireActivity(), SelectContact::class.java)
            resultLauncher.launch(intent)
        }
        binding.actvOperator.setOnClickListener {
//            mListener.operatorSelection()
            findNavController().navigate(R.id.action_mobileRechargeHost_to_selectOperator)
        }

        binding.etMobileNumber.doOnTextChanged { text, start, before, count ->
            if (text.toString().length==10){
                viewModel.rechargeMobileNo.postValue(text.toString())
                viewModel.getCircleNOperatorOfMobileNo(text.toString())
            }
        }
        binding.btnConfirm.setOnClickListener {
            val number = binding.etMobileNumber.text.toString().trim()
            viewModel.rechargeMobileNo.postValue(number)
            val amount = binding.etRechargeAmount.text.toString().trim()
            if (amount.toInt()>0){
                viewModel.rechargeAmount.postValue(amount.toInt())

                val sheet = PaymentMethods(this)
                sheet.show(parentFragmentManager,sheet.tag)
            }else{
                showToast("Enter valid recharge amount!!!")
            }
        }

        binding.btnViewPlan.setOnClickListener {
            val sheet = ChooseRechargePlans()
            sheet.show(parentFragmentManager,sheet.tag)
        }

        binding.btnBestOffer.setOnClickListener {
            val sheet = ChooseRechargePlans(PlanType.SPECIAL_PLAN)
            sheet.show(parentFragmentManager,sheet.tag)
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })

        viewModel.userRoleID.observe(this,{
            roleId = it
        })

        viewModel.selectedOperator.observe(this,{
            binding.actvOperator.setText(it)
        })
        viewModel.selectedCircle.observe(viewLifecycleOwner,{

        })

        viewModel.rechargeAmount.observe(viewLifecycleOwner,{
            binding.etRechargeAmount.setText(it.toString())
        })

        viewModel.rechargeMobileNo.observe(viewLifecycleOwner,{
            if (it.length==10) binding.btnConfirm.isEnabled = true
        })


        viewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        it.Operator?.let {operator->
                            viewModel.selectedOperator.postValue(operator)
                        }

                        it.circle?.let { circle ->
                            if (circle.length > 2) viewModel.selectedCircle.postValue(circle)
                            else viewModel.selectedCircle.postValue(circle)
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


        viewModel.walletBalance.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.rechargeAmount.value!!){
                            showToast("Insufficient Wallet Balance !!!")
                        }else{
                            recharge = MobileRechargeModel()
                            recharge.UserID = userId
                            recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                            recharge.ServiceTypeID = 1
                            recharge.WalletTypeID = 1
                            recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                            recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
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

        viewModel.pCash.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.rechargeAmount.value!!){
                            showToast("Insufficient Wallet Balance !!!")


                        }else{
                            recharge = MobileRechargeModel()
                            recharge.UserID = userId
                            recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                            recharge.ServiceTypeID = 1
                            recharge.WalletTypeID = 4
                            recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                            recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
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

        viewModel.checkSum.observe(viewLifecycleOwner, { _result ->
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

        viewModel.addUsedServiceDetailResponse.observe(viewLifecycleOwner, { _result ->
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

        viewModel.usedServiceRequestId.observe(viewLifecycleOwner, { _result ->
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



        viewModel.mobileRechargeModel.observe(viewLifecycleOwner, { _result ->
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

        viewModel.addPaymentTransResponse.observe(viewLifecycleOwner, { _result ->
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

        viewModel.walletChargeDeducted.observe(viewLifecycleOwner, { _result ->
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

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        serviceRepository.selectedPaymentMethod = method
        when(method){
            PaymentEnum.WALLET->viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH->viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PAYTM->{
                recharge = MobileRechargeModel()
                recharge.UserID = userId
                recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                recharge.ServiceTypeID = 1
                recharge.WalletTypeID = 3
                recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                recharge.ServiceField1 = ""
                recharge.ServiceProviderID = 3
                recharge.Status = "Received"
                recharge.TransTypeID = 9
                viewModel.addUsedServiceDetail(recharge)

            }
        }
    }

    fun startPayment() {
        gatewayOrderId = createRandomOrderId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account= recharge.RequestID,
                amount = viewModel.rechargeAmount.value.toString(),
                callbackurl = Constants.PAYTM_CALLBACK_URL,
                userid = userId
            )
        )

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

    interface MobileRechargeInterface{
        fun operatorSelection()
    }
}