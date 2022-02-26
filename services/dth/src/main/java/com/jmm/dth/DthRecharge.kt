package com.jmm.dth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.core.utils.Constants
import com.jmm.core.utils.createRandomOrderId
import com.jmm.core.utils.getTodayDate
import com.jmm.core.utils.showActionDialog
import com.jmm.dth.databinding.FragmentDthRechargeBinding
import com.jmm.model.OperationResultModel
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.repository.ServiceRepository
import com.jmm.util.BaseFragment
import com.jmm.util.OperationResultDialog
import com.jmm.util.Status
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DthRecharge : BaseFragment<FragmentDthRechargeBinding>(FragmentDthRechargeBinding::inflate),
    OperationResultDialog.OperationResultDialogCallback, PaytmPaymentTransactionCallback,
    PaymentMethods.PaymentMethodsInterface, DthActivityInterface {

    private val viewModel by activityViewModels<DTHActivityViewModel>()
    @Inject
    lateinit var serviceRepository: ServiceRepository
    private lateinit var userId: String
    private var roleId = 0
    private var gatewayOrderId: String = ""

    override fun onResume() {
        super.onResume()
        (requireActivity() as DthActivity).setDthRechargeActivityListener(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivOperatorLogo.setImageResource(viewModel.selectedOperator.value!!.imageUrl as Int)
            etAccountId.doOnTextChanged { text, start, before, count ->
                btnSearch.isVisible = !text.isNullOrEmpty()
            }

            etRechargeAmount.doOnTextChanged { text, start, before, count ->
                btnConfirm.isEnabled = !text.isNullOrEmpty()
            }
            btnSearch.setOnClickListener {
                val accountId = etAccountId.text.toString().trim()
                val operatorCode = viewModel.selectedOperator.value!!.operatorCode
                if (operatorCode != null) {
                    viewModel.getDthCustomerDetails(accountId,operatorCode)
                }
            }

            cp50.setOnClickListener {
                etRechargeAmount.setText("50")
            }

            cp100.setOnClickListener {
                etRechargeAmount.setText("100")
            }

            cp200.setOnClickListener {
                etRechargeAmount.setText("200")
            }
        }

        binding.btnConfirm.setOnClickListener {
            if(userId.isEmpty()){
                checkAuthorization()
            }else{
                val number = binding.etAccountId.text.toString().trim()
                viewModel.rechargeMobileNo.postValue(number)
                val amount = binding.etRechargeAmount.text.toString().trim()
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
    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userId = it
        }

        viewModel.userRoleID.observe(this) {
            roleId = it
        }

        viewModel.rechargeMobileNumber.observe(viewLifecycleOwner) {
            binding.etAccountId.setText(it)
        }

        viewModel.rechargeAmount.observe(viewLifecycleOwner) {
            binding.etRechargeAmount.setText(it.toString())
        }
        viewModel.dthCustomerDetail.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.customerName.isNullOrEmpty()) {
                            binding.containerCustomerDetails.isVisible = false
                        } else {
                            binding.containerCustomerDetails.isVisible = true
                            binding.apply {
                                tvCustomerName.text = it.customerName
                                tvDueDate.text = it.NextRechargeDate
                                tvBalance.text = it.Balance
                                tvMonthlyRecharge.text = it.MonthlyRecharge
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

        viewModel.dthRechargePageState.observe(viewLifecycleOwner) {state->
            displayLoading(false)
            hideLoadingDialog()
            when (state) {
                is DthRechargePageState.Error -> showActionDialog(
                    requireActivity(),
                    DialogType.ERROR,
                    "Oops !!!",
                    "Something went wrong! Please try again.",
                    "Okay"
                ) {
                    // do things here
                }
                DthRechargePageState.CancelledGateway -> {
                    showToast("Cancelled!!!")
                }
                DthRechargePageState.InitiatingTransaction -> TODO()
                DthRechargePageState.InsufficientBalance -> showActionDialog(
                    requireActivity(),
                    DialogType.ERROR,
                    "Insufficient balance!!",
                    "Your wallet balance is low.",
                    "Okay"
                ) {
                    // do things here
                }
                DthRechargePageState.Loading -> displayLoading(true)

                is DthRechargePageState.Processing ->showLoadingDialog(state.msg)
                is DthRechargePageState.ReceivedChecksum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.MERCHANT_ID,
                        state.checksum,
                        viewModel.rechargeAmount.value.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    viewModel.dthRechargePageState.postValue(DthRechargePageState.Processing("Opening gateway..."))
                    processPaytmTransaction(paytmOrder)

                }
                DthRechargePageState.RequestingGateway -> TODO()
                is DthRechargePageState.ReceivedGatewayResponse ->{
                    when (state.paytmResponseModel.STATUS) {
                        "SUCCESS","FAILURE" -> {
                            viewModel.recharge = MobileRechargeModel()
                            viewModel.recharge.UserID = userId
                            viewModel.recharge.RoleID = roleId
                            viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                            viewModel.recharge.ServiceTypeID = 2
                            viewModel.recharge.WalletTypeID = WalletType.OnlinePayment.id
                            viewModel.recharge.OperatorCode = viewModel.selectedOperator.value.toString()
                            viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                            viewModel.recharge.ServiceField1 = ""
                            viewModel.recharge.ServiceProviderID = 3
                            viewModel.recharge.Status = "Received"
                            viewModel.recharge.TransTypeID = 9
                            viewModel.callDthRechargeService(state.paytmResponseModel)

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

                is DthRechargePageState.OnRechargeResponseReceived ->{
                    when(state.response.Status){
                        "SUCCESS"->{
                            showFullScreenDialog(
                                OperationResultModel(
                                    title1 = "Recharge done successfully!!",
                                    amount = viewModel.rechargeAmount.value.toString(),
                                    status = "Payment Successful",
                                    timestamp = getTodayDate(),
                                    state.response.RequestID!!,
                                    animationUrl = R.raw.success_animation
                                ), operationResultDialogCallback = this
                            )
                        }
                        "PENDING"->{

                            showFullScreenDialog(
                                OperationResultModel(
                                    title1 = "Recharge pending !!!",
                                    amount = viewModel.rechargeAmount.value.toString(),
                                    status = "Payment pending",
                                    timestamp = getTodayDate(),
                                    state.response.RequestID!!,
                                    animationUrl = R.raw.error_animation
                                ), operationResultDialogCallback = this
                            )
                        }
                        "FAILURE","FAILED"->{
                            showFullScreenDialog(
                                OperationResultModel(
                                    title1 = "Recharge failed !!!",
                                    amount = viewModel.rechargeAmount.value.toString(),
                                    status = "Payment Failed",
                                    timestamp = getTodayDate(),
                                    state.response.RequestID!!,
                                    animationUrl = R.raw.error_animation
                                ), operationResultDialogCallback = this
                            )
                        }
                    }
                }
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
                viewModel.recharge.ServiceTypeID = 2
                viewModel.recharge.WalletTypeID = WalletType.Wallet.id
                viewModel.recharge.OperatorCode = viewModel.selectedOperator.value.toString()
                viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                viewModel.recharge.ServiceField1 = ""
                viewModel.recharge.ServiceProviderID = 3
                viewModel.recharge.Status = "Received"
                viewModel.recharge.TransTypeID = 9
                viewModel.callDthRechargeService(null)
            }
            PaymentEnum.PCASH -> {
                viewModel.recharge = MobileRechargeModel()
                viewModel.recharge.UserID = userId
                viewModel.recharge.MobileNo = viewModel.rechargeMobileNo.value!!
                viewModel.recharge.ServiceTypeID = 2
                viewModel.recharge.WalletTypeID = WalletType.PCash.id
                viewModel.recharge.OperatorCode = viewModel.selectedOperator.value.toString()
                viewModel.recharge.RechargeAmt = viewModel.rechargeAmount.value!!.toDouble()
                viewModel.recharge.ServiceField1 = ""
                viewModel.recharge.ServiceProviderID = 3
                viewModel.recharge.Status = "Received"
                viewModel.recharge.TransTypeID = 9
                viewModel.callDthRechargeService(null)
            }
            PaymentEnum.PAYTM -> { startPayment() }
        }
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
//            transactionManager.startTransaction(requireActivity(), 100)

            transactionManager.startTransactionAfterCheckingLoginStatus(requireActivity(),Constants.MERCHANT_ID, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onTransactionResponse(p0: Bundle?) {
        Timber.d("onTransactionResponse : ${p0.toString()}")

        p0?.let {
            val paytmResponseModel = PaymentMethods.getPaytmResponse(it)
            viewModel.dthRechargePageState.postValue(DthRechargePageState.ReceivedGatewayResponse(paytmResponseModel))
        }

    }

    override fun networkNotAvailable() {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }

    override fun onResultDialogDismiss() {

    }

    override fun onAppInvokeResponse(response: JSONObject, invoke: String) {
        val paytmResponseModel =PaymentMethods.getPaytmResponse(response)
        viewModel.dthRechargePageState.postValue(DthRechargePageState.ReceivedGatewayResponse(paytmResponseModel))
    }

}