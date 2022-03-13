package com.jmm.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.checkout.databinding.FragmentFinalCheckoutBinding
import com.jmm.core.utils.Constants
import com.jmm.core.utils.createRandomOrderId
import com.jmm.core.utils.setAmount
import com.jmm.core.utils.showActionDialog
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.PaymentModes
import com.jmm.model.myEnums.PaymentStatus
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.shopping_models.CustomerOrder
import com.jmm.payment_gateway.PaymentMethods
import com.jmm.util.BaseFragment
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FinalCheckout :
    BaseFragment<FragmentFinalCheckoutBinding>(FragmentFinalCheckoutBinding::inflate),
    PaymentMethods.PaymentMethodsInterface, PaytmPaymentTransactionCallback {

    private val viewModel by activityViewModels<CheckoutViewModel>()

    private lateinit var userId: String
    private var roleId = 0
    private lateinit var gatewayOrderId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            val sheet = PaymentMethods(this, true)
            sheet.show(parentFragmentManager, sheet.tag)
        }
    }

    override fun subscribeObservers() {
        viewModel.grandTotalAmount.observe(viewLifecycleOwner) {
            binding.tvAmount.setAmount(it)
        }
        viewModel.isValid.observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled = it
        }

        viewModel.userID.observe(this) {
            userId = it
        }

        viewModel.userRoleID.observe(this) {
            roleId = it
        }

        viewModel.pageState.observe(viewLifecycleOwner) { state ->

            displayLoading(false)
            when (state) {
                CheckoutPageState.EmptyCart -> {
                    findNavController().navigateUp()
                    /*binding.root.children.forEach {
                        it.isVisible = false
                    }*/
                }
                is CheckoutPageState.Error -> {}
                CheckoutPageState.Idle -> {}
                CheckoutPageState.Loading -> displayLoading(false)
                is CheckoutPageState.ReceivedCartItems -> {

                }
                CheckoutPageState.CancelledGateway -> {
                    showToast("Payment Cancelled !!!")
                }
                CheckoutPageState.InitiatingTransaction -> {
                    showLoadingDialog("Initiating transaction...")
                }
                CheckoutPageState.InsufficientBalance -> {
                    showActionDialog(
                        requireActivity(),
                        DialogType.SUCCESS,
                        "Insufficient Balance!!!",
                        "You don't have enough balance to process this order. Kindly add money to wallet."
                    )
                }


                is CheckoutPageState.OrderSuccessful -> {
                    val intent = Intent(requireActivity(),OrderSuccessful::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is CheckoutPageState.Processing -> {
                    showLoadingDialog(state.msg)
                }
                is CheckoutPageState.ReceivedChecksum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.MERCHANT_ID,
                        state.checksum,
                        viewModel.grandTotalAmount.value.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }
                is CheckoutPageState.ReceivedGatewayResponse -> {
                    when (viewModel.paytmResponseModel.STATUS) {
                        "SUCCESS" -> {
                            viewModel.customerOrder = CustomerOrder(
                                ShippingAddressId = viewModel.selectedAddressId.value,
                                UserID = userId,
                                Total = viewModel.totalAmount.value,
                                Discount = viewModel.appliedDiscount.value,
                                Shipping = viewModel.shippingCharge.value,
                                Tax = viewModel.tax.value,
                                GrandTotal = viewModel.grandTotalAmount.value,
                                Promo = viewModel.appliedCoupon.value!!.Code,
                                PaymentStatusId = PaymentStatus.Pending.id,
                                WalletTypeId = WalletType.OnlinePayment.id,
                                PaymentMode = PaymentModes.Online.id
                            )

                            viewModel.createCustomerOrder(viewModel.customerOrder)
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
                is CheckoutPageState.ReceivedUpdatedPaymentStatus -> {

                }
                CheckoutPageState.RequestingGateway -> {
                    showLoadingDialog("Opening gateway...")
                }
            }

        }
    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        when (method) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> {
                startPayment()
            }
            PaymentEnum.COD -> {
                viewModel.customerOrder = CustomerOrder(
                    ShippingAddressId = viewModel.selectedAddressId.value,
                    UserID = userId,
                    Total = viewModel.totalAmount.value,
                    Discount = viewModel.appliedDiscount.value,
                    Shipping = viewModel.shippingCharge.value,
                    Tax = viewModel.tax.value,
                    GrandTotal = viewModel.grandTotalAmount.value,
                    Promo = viewModel.appliedCoupon.value!!.Code,
                    PaymentStatusId = PaymentStatus.Pending.id,
                    WalletTypeId = WalletType.CashOnDelivery.id,
                    PaymentMode = PaymentModes.CashOnDelivery.id
                )

                viewModel.createCustomerOrder(viewModel.customerOrder)
            }
        }
    }

    private fun startPayment() {
        gatewayOrderId = createRandomOrderId()

        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account = gatewayOrderId,
                amount = viewModel.grandTotalAmount.toString(),
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
            viewModel.paytmResponseModel = PaymentMethods.getPaytmResponse(it)
            viewModel.pageState.postValue(CheckoutPageState.ReceivedGatewayResponse(viewModel.paytmResponseModel))


        }

    }

    override fun networkNotAvailable() {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("networkNotAvailable : No Internet :(")
    }

    override fun onErrorProceed(p0: String?) {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("onErrorProceed : ${p0.toString()}")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("clientAuthenticationFailed : ${p0.toString()}")
    }

    override fun someUIErrorOccurred(p0: String?) {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("someUIErrorOccurred : ${p0.toString()}")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("onErrorLoadingWebPage : $p0 \n $p1 \n $p2")
    }

    override fun onBackPressedCancelTransaction() {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("onBackPressedCancelTransaction : Back pressed :(")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        viewModel.pageState.postValue(CheckoutPageState.CancelledGateway)
        Timber.d("onTransactionResponse : ${p0.toString()} \n ${p1.toString()}")
    }

}