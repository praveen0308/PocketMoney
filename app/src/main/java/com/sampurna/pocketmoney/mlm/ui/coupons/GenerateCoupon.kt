package com.sampurna.pocketmoney.mlm.ui.coupons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sampurna.pocketmoney.common.PaymentMethods
import com.sampurna.pocketmoney.databinding.FragmentGenerateCouponBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.mlm.viewmodel.ManageCouponsViewModel
import com.sampurna.pocketmoney.utils.*
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import com.sampurna.pocketmoney.utils.myEnums.WalletType
import com.jmm.brsap.dialog_builder.DialogType
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GenerateCoupon : BaseFragment<FragmentGenerateCouponBinding>(FragmentGenerateCouponBinding::inflate),
    PaymentMethods.PaymentMethodsInterface, PaytmPaymentTransactionCallback {

    private var roleId = 0
    private val viewModel by viewModels<ManageCouponsViewModel>()

    private var userId = ""
    private var gatewayOrderId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnIncrement.setOnClickListener {
                viewModel.incrementNoOfCoupons()
            }

            btnDecrement.setOnClickListener {
                viewModel.decrementNoOfCoupons()
            }

            btnPay.setOnClickListener {
                val sheet = PaymentMethods(this@GenerateCoupon)
                sheet.show(parentFragmentManager, sheet.tag)
                /*val sheet = PaymentPortal(this@GenerateCoupon,viewModel.amountPayable)
                sheet.show(parentFragmentManager,sheet.tag)*/
            }
        }
    }
    override fun subscribeObservers() {
        viewModel.noOfCoupons.observe(viewLifecycleOwner,{
            viewModel.amountPayable = (it*300).toDouble()
            binding.tvNoOfCoupons.text = it.toString()
            binding.btnPay.text = "Pay ₹${viewModel.amountPayable}"
        })

        viewModel.userId.observe(viewLifecycleOwner,{
            if (it.isNullOrEmpty()){

            }
            else{
                userId = it
            }
        })

        viewModel.userRoleID.observe(this, {
            roleId = it
        })

        viewModel.progressStatus.observe(viewLifecycleOwner, {
            displayLoading(false)
            hideLoadingDialog()
            when (it) {
                ERROR -> {
                    showActionDialog(
                        requireActivity(),
                        DialogType.ERROR,
                        "Oops !!!",
                        "Something went wrong! Please try again.",
                        "Okay"
                    ) {
                        // do things here
                    }
                }
                CHECKING_WALLET_BALANCE -> {
                    showLoadingDialog("Checking wallet balance...")
                }
                INSUFFICIENT_BALANCE -> {
                    showActionDialog(
                        requireActivity(),
                        DialogType.ERROR,
                        "Insufficient balance!!",
                        "Your wallet balance is low.",
                        "Okay"
                    ) {
                        // do things here
                    }
                }
                INITIATING_TRANSACTION -> {
                    showLoadingDialog("Initiating transaction...")
                }
                CHECKSUM_RECEIVED -> {
                    displayLoading(false)
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.P_MERCHANT_ID,
                        viewModel.transactionToken,
                        viewModel.amountPayable.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }

                SUCCESSFUL->{
                    showActionDialog(
                        requireActivity(),
                        DialogType.SUCCESS,
                        "Success!!!",
                        "${viewModel.noOfCoupons} Coupons generated successfully..",
                        "Great"
                    ) {
                        findNavController().navigateUp()
                    }
                }

                FAILED->{
                    showActionDialog(
                        requireActivity(),
                        DialogType.SUCCESS,
                        "Failed!!!",
                        "Something went wrong!!! Unable to generate coupons.",
                        "Okay"
                    ) {
                        findNavController().navigateUp()
                    }
                }
                PROCESSING -> {
                    showLoadingDialog()
                }
            }

        })

    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        viewModel.selectedPaymentMethod = method
        when (viewModel.selectedPaymentMethod) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> { startPayment() }
        }
    }

    private fun startPayment() {
        gatewayOrderId = createRandomOrderId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account = gatewayOrderId,
                amount = viewModel.amountPayable.toString(),
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
            transactionManager.startTransaction(requireActivity(), 3)
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
                    viewModel.generateNewCoupons(userId,WalletType.PCash.id,viewModel.noOfCoupons.value!!)

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
                    showActionDialog(requireActivity(), DialogType.ERROR, "Oops!",
                        "Something went wrong!! Contact admin...", mListener = {
                            requireActivity().finish()
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


    companion object {
        const val LOADING = 100
        const val PENDING = 101
        const val ERROR = 102
        const val FAILED = 103
        const val SUCCESSFUL = 104
        const val CHECKING_WALLET_BALANCE = 105
        const val INSUFFICIENT_BALANCE = 106
        const val GENERATING_COUPON = 108
        const val CHECKSUM_RECEIVED = 108
        const val START_PAYMENT_GATEWAY = 109
        const val PROCESSING = 110
        const val INITIATING_TRANSACTION = 111

    }

}