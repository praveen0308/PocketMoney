package com.jmm.mobile_recharge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.core.TaskResultDialog
import com.jmm.core.TaskResultDialog.Companion.KEY_ACCOUNT_NUMBER
import com.jmm.core.TaskResultDialog.Companion.KEY_AMOUNT
import com.jmm.core.TaskResultDialog.Companion.KEY_HEADING
import com.jmm.core.TaskResultDialog.Companion.KEY_PAYMENT_STATUS
import com.jmm.core.TaskResultDialog.Companion.KEY_REF_ID
import com.jmm.core.TaskResultDialog.Companion.KEY_STATUS
import com.jmm.core.TaskResultDialog.Companion.KEY_SUBTITLE
import com.jmm.core.TaskResultDialog.Companion.KEY_WALLET_TYPE_ID
import com.jmm.core.utils.*
import com.jmm.mobile_recharge.databinding.FragmentRechargeBinding
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.PlanType
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.repository.ServiceRepository
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class Recharge(val mListener: MobileRechargeInterface) :
    BaseFragment<FragmentRechargeBinding>(FragmentRechargeBinding::inflate),
    PaymentMethods.PaymentMethodsInterface, PaytmPaymentTransactionCallback,
    NewRechargeActivityInterface {

    private val viewModel by activityViewModels<MobileRechargeViewModel>()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var mCircle: String = "Mumbai"
    private var mOperator: String = "Jio"

    private var previousMobileNumber = ""
    private val mRequestCode = 100
    @Inject
    lateinit var serviceRepository: ServiceRepository
    private var userId: String = ""
    private var roleId = 0

    //    private lateinit var recharge : MobileRechargeModel
    private var gatewayOrderId: String = ""

    private val circles = mutableListOf<String>(
        "Maharashtra Goa",
        "Mumbai",
        "Andhra Pradesh Telangana",
        "Assam",
        "Bihar Jharkhand",
        "Chennai",
        "Delhi NCR",
        "Gujarat",
        "Haryana",
        "Himachal Pradesh",
        "Jammu Kashmir",
        "Karnataka",
        "Kerala",
        "Kolkata",
        "Madhya Pradesh Chhattisgarh",
        "North East",
        "Orissa",
        "Punjab",
        "Rajasthan",
        "Tamil Nadu",
        "UP East",
        "UP West",
        "West Bengal",
    )

    override fun onResume() {
        super.onResume()
        (requireActivity() as NewRechargeActivity).setNewRechargeActivityListener(this)
    }
    //    private lateinit var viewModel.paytmResponseModel: PaytmResponseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK) {
                    val number = data!!.getStringExtra("Number")!!
                    binding.etMobileNumber.setText(number)
                } else {
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
            if (text.toString().length == 10) {
                viewModel.rechargeMobileNo.postValue(text.toString())
                viewModel.getCircleNOperatorOfMobileNo(text.toString())
            }
        }
        binding.btnConfirm.setOnClickListener {
            if(userId.isEmpty()){
                checkAuthorization()
            }else{
                val number = binding.etMobileNumber.text.toString().trim()
                viewModel.rechargeMobileNo.postValue(number)
                val amount = binding.etRechargeAmount.text.toString()
                if (amount.isNullOrEmpty()) {
                    showToast("Enter valid recharge amount!!!")
                } else {
                    if (amount.toInt() > 0) {
                        viewModel.rechargeAmount.postValue(amount.toInt())
                        val sheet = PaymentMethods(this)
                        sheet.show(parentFragmentManager, sheet.tag)
                    } else {
                        showToast("Enter valid recharge amount!!!")
                    }
                }

            }

        }

        binding.btnViewPlan.setOnClickListener {
            if (viewModel.rechargeMobileNo.value.isNullOrEmpty() || viewModel.rechargeMobileNo.value!!.length != 10) {
                showToast("Enter a valid mobile number...")
            } else {
                previousMobileNumber = viewModel.rechargeMobileNo.value.toString()
                viewModel.getMobileSimplePlanList(
                    viewModel.selectedCircle.value!!,
                    viewModel.selectedOperator.value!!
                )

                /*if (previousMobileNumber == viewModel.rechargeMobileNo.value.toString()){
                    val sheet = ChooseRechargePlans()
                    sheet.show(parentFragmentManager, sheet.tag)
                }else{
                    previousMobileNumber = viewModel.rechargeMobileNo.value.toString()
                    viewModel.getMobileSimplePlanList(viewModel.selectedCircle.value!!, viewModel.selectedOperator.value!!)
                }*/
            }
        }

        binding.btnBestOffer.setOnClickListener {
            if (viewModel.rechargeMobileNo.value.isNullOrEmpty()) {
                showToast("Enter a valid mobile number...")
            } else {
                previousMobileNumber = viewModel.rechargeMobileNo.value.toString()
                viewModel.getMobileSpecialPlanList(
                    viewModel.rechargeMobileNo.value!!,
                    viewModel.selectedOperator.value!!
                )
                /*if (previousMobileNumber == viewModel.rechargeMobileNo.value.toString()){
                    val sheet = ChooseRechargePlans(PlanType.SPECIAL_PLAN)
                    sheet.show(parentFragmentManager, sheet.tag)
                }else{
                    previousMobileNumber = viewModel.rechargeMobileNo.value.toString()
                    viewModel.getMobileSpecialPlanList(viewModel.rechargeMobileNo.value!!, viewModel.selectedOperator.value!!)
                }*/


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

        viewModel.selectedOperator.observe(this) {
            binding.actvOperator.setText(it)
        }
        viewModel.selectedCircle.observe(viewLifecycleOwner) {

        }

        viewModel.rechargeAmount.observe(viewLifecycleOwner) {
            binding.etRechargeAmount.setText(it.toString())
        }

        viewModel.rechargeMobileNumber.observe(viewLifecycleOwner) {
            binding.etMobileNumber.setText(it)
        }
        viewModel.rechargeMobileNo.observe(viewLifecycleOwner) {

            if (it.length == 10) binding.btnConfirm.isEnabled = true
        }

        viewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        if (it.Operator.isNullOrEmpty()) {
                            viewModel.selectedOperator.postValue("Jio")
                            binding.tilOperator.error = "Not a jio operator! then choose manually."
                        } else {
                            viewModel.selectedOperator.postValue(it.Operator)
                            binding.tilOperator.error = null
                        }

                        if (it.circle.isNullOrEmpty()) {
                            viewModel.selectedCircle.postValue(circles[0])
                        } else {
                            if (circles.contains(it.circle)) {
                                viewModel.selectedCircle.postValue(it.circle)
                            } else {
                                viewModel.selectedCircle.postValue(circles[0])
                            }
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
        }

        viewModel.mobileSimplePlanList.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val sheet = ChooseRechargePlans()
                        sheet.show(parentFragmentManager, sheet.tag)
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
        }

        viewModel.mobileSpecialPlanList.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val sheet = ChooseRechargePlans(PlanType.SPECIAL_PLAN)
                        sheet.show(parentFragmentManager, sheet.tag)
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
        }


        viewModel.rechargePageState.observe(viewLifecycleOwner) {state->
            displayLoading(false)
            hideLoadingDialog()
            when (state) {
                is MobileRechargePageState.Error -> showActionDialog(
                    requireActivity(),
                    DialogType.ERROR,
                    "Oops !!!",
                    "Something went wrong! Please try again.",
                    "Okay"
                ) {
                    // do things here
                }
                MobileRechargePageState.CancelledGateway -> {
                    showToast("Cancelled!!!")
                }
                MobileRechargePageState.InitiatingTransaction -> TODO()
                MobileRechargePageState.InsufficientBalance -> showActionDialog(
                    requireActivity(),
                    DialogType.ERROR,
                    "Insufficient balance!!",
                    "Your wallet balance is low.",
                    "Okay"
                ) {
                    // do things here
                }
                MobileRechargePageState.Loading -> displayLoading(true)

                is MobileRechargePageState.Processing ->showLoadingDialog(state.msg)
                is MobileRechargePageState.ReceivedChecksum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.MERCHANT_ID,
                        state.checksum,
                        viewModel.rechargeAmount.value.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    viewModel.rechargePageState.postValue(MobileRechargePageState.Processing("Opening gateway..."))
                    processPaytmTransaction(paytmOrder)

                }
                MobileRechargePageState.RequestingGateway -> TODO()
                is MobileRechargePageState.ReceivedGatewayResponse ->{
                    when (state.paytmResponseModel.STATUS) {
                        "SUCCESS","FAILURE" -> {
                            viewModel.recharge = MobileRechargeModel()
                            viewModel.recharge.UserID = userId
                            viewModel.recharge.RoleID = roleId
                            viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                            viewModel.recharge.ServiceTypeID = 1
                            viewModel.recharge.WalletTypeID = WalletType.OnlinePayment.id
                            viewModel.recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                            viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                            viewModel.recharge.ServiceField1 = ""
                            viewModel.recharge.ServiceProviderID = 3
                            viewModel.recharge.Status = "Received"
                            viewModel.recharge.TransTypeID = 9
                           viewModel.callMobileRechargeService(state.paytmResponseModel)

                        }

                        "CANCELLED" -> {
                            showToast("Transaction Cancelled !!!")
                        }
                        else -> {
                            showActionDialog(requireActivity(), DialogType.ERROR, "Oops!",
                                "Something went wrong!! Try again later...", mListener = {
                                    requireActivity().finish()
                                })
                        }
                    }
                }

                is MobileRechargePageState.OnRechargeResponseReceived ->{
                    when(state.response.Status){
                        "SUCCESS"->{
                            val bundle = Bundle()
                            bundle.putString(KEY_HEADING, "Recharge Successful!!!")
                            bundle.putString(
                                KEY_SUBTITLE,
                                "Your ${viewModel.selectedOperator.value} prepaid was successfully recharged."
                            )
                            bundle.putString(
                                KEY_SUBTITLE,
                                state.response.Message
                            )
                            bundle.putInt(KEY_STATUS, TaskResultDialog.SUCCESS)
                            bundle.putString(KEY_REF_ID, state.response.RequestID)
                            bundle.putString(KEY_ACCOUNT_NUMBER, viewModel.recharge.MobileNo.toString())
                            bundle.putString(KEY_AMOUNT, viewModel.rechargeAmount.value.toString())
                            bundle.putString(KEY_PAYMENT_STATUS, state.response.Status)
                            bundle.putInt(KEY_WALLET_TYPE_ID, viewModel.recharge.WalletTypeID!!)
                            showTaskResultDialog(bundle, parentFragmentManager)
                        }
                        "PENDING"->{
                            val bundle = Bundle()
                            bundle.putString(KEY_HEADING, "Recharge Pending!!!")
                            bundle.putString(
                                KEY_SUBTITLE,
                                "Your ${viewModel.selectedOperator.value} prepaid recharge is pending."
                            )
                            bundle.putString(
                                KEY_SUBTITLE,
                                state.response.Message
                            )
                            bundle.putInt(KEY_STATUS, TaskResultDialog.PENDING)
                            bundle.putString(KEY_REF_ID, state.response.RequestID)
                            bundle.putString(KEY_ACCOUNT_NUMBER, viewModel.recharge.MobileNo.toString())
                            bundle.putString(KEY_AMOUNT, viewModel.rechargeAmount.value.toString())
                            bundle.putString(KEY_PAYMENT_STATUS, state.response.Status)
                            bundle.putInt(KEY_WALLET_TYPE_ID, viewModel.recharge.WalletTypeID!!)
                            showTaskResultDialog(bundle, parentFragmentManager)
                        }
                        "FAILURE","FAILED"->{
                            val bundle = Bundle()
                            bundle.putString(KEY_HEADING, "Recharge Failed!!!")
                            bundle.putString(
                                KEY_SUBTITLE,
                                "Your ${viewModel.selectedOperator.value} prepaid was unfortunately failed."
                            )
                            bundle.putString(
                                KEY_SUBTITLE,
                                state.response.Message
                            )
                            bundle.putInt(KEY_STATUS, TaskResultDialog.FAILURE)
                            bundle.putString(KEY_REF_ID, state.response.RequestID)
                            bundle.putString(KEY_ACCOUNT_NUMBER, viewModel.recharge.MobileNo.toString())
                            bundle.putString(KEY_AMOUNT, viewModel.rechargeAmount.value.toString())
                            bundle.putString(KEY_PAYMENT_STATUS, state.response.Status)
                            bundle.putInt(KEY_WALLET_TYPE_ID, viewModel.recharge.WalletTypeID!!)
                            showTaskResultDialog(bundle, parentFragmentManager)
                        }
                    }
                }
                else -> {}
            }

        }

    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        serviceRepository.selectedPaymentMethod = method
        when (method) {
            PaymentEnum.WALLET ->{
                viewModel.recharge = MobileRechargeModel()
                viewModel.recharge.UserID = userId
                viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                viewModel.recharge.ServiceTypeID = 1
                viewModel.recharge.WalletTypeID = WalletType.Wallet.id
                viewModel.recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                viewModel.recharge.ServiceField1 = ""
                viewModel.recharge.ServiceProviderID = 3
                viewModel.recharge.Status = "Received"
                viewModel.recharge.TransTypeID = 9
                viewModel.callMobileRechargeService(null)
            }
            PaymentEnum.PCASH -> {
                viewModel.recharge = MobileRechargeModel()
                viewModel.recharge.UserID = userId
                viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                viewModel.recharge.ServiceTypeID = 1
                viewModel.recharge.WalletTypeID = WalletType.PCash.id
                viewModel.recharge.OperatorCode = getMobileOperatorCode(viewModel.selectedOperator.value!!).toString()
                viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                viewModel.recharge.ServiceField1 = ""
                viewModel.recharge.ServiceProviderID = 3
                viewModel.recharge.Status = "Received"
                viewModel.recharge.TransTypeID = 9
                viewModel.callMobileRechargeService(null)
            }
            PaymentEnum.PAYTM -> {
                startPayment()
            }
        }
    }

    interface MobileRechargeInterface {
        fun operatorSelection()
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
            transactionManager.setAppInvokeEnabled(true)

            transactionManager.startTransaction(requireActivity(), mRequestCode)
//            transactionManager.startTransactionAfterCheckingLoginStatus(requireActivity(),Constants.MERCHANT_ID, mRequestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTransactionResponse(p0: Bundle?) {
        Timber.d("onTransactionResponse : ${p0.toString()}")

        p0?.let {
            val paytmResponseModel = PaymentMethods.getPaytmResponse(it)

            viewModel.rechargePageState.postValue(MobileRechargePageState.ReceivedGatewayResponse(paytmResponseModel))

        }

    }

    override fun networkNotAvailable() {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }

    override fun onAppInvokeResponse(response: JSONObject, invoke: String) {
        val paytmResponseModel =PaymentMethods.getPaytmResponse(response)
        viewModel.rechargePageState.postValue(MobileRechargePageState.ReceivedGatewayResponse(paytmResponseModel))
    }

}