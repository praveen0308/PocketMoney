package com.example.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityNewCheckoutBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.paymentgateway.PaymentPortal
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.PaymentModes
import com.example.pocketmoney.utils.myEnums.PaymentStatus
import com.example.pocketmoney.utils.myEnums.WalletType
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCheckout : BaseActivity<ActivityNewCheckoutBinding>(ActivityNewCheckoutBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener,
    PaymentPortal.PaymentPortalCallback {

    private val viewModel by viewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository


    private lateinit var userId: String
    private var roleId = 0
    private lateinit var gatewayOrderId: String

    private lateinit var paytmResponseModel: PaytmResponseModel
    private lateinit var orderNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarCheckoutOrder.setApplicationToolbarListener(this)
        binding.btnContinue.setOnClickListener {
            it.isEnabled = false
            val bottomSheet = PaymentPortal(this, viewModel.grandTotal, true)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    override fun subscribeObservers() {
        viewModel.amountPayable.observe(this, {
            binding.btnContinue.text = "Pay ₹ $it"
        })
        viewModel.userId.observe(this, {
            userId = it
        })

        viewModel.userRoleID.observe(this, {
            roleId = it
        })
        checkoutRepository.appliedCouponCode.observe(this, {
            if (!it.isNullOrEmpty()) {

            }
        })
        viewModel.progressStatus.observe(this,{
            when(it){
                LOADING->{
                    displayLoading(true)
                }
                SUCCESS->{
                    displayLoading(false)
                }
                ERROR->{
                    displayLoading(false)
                }
                CREATING_ORDER_NUMBER->{
                    displayLoading(false)
                }
                ORDER_SUCCESSFUL->{
                    displayLoading(false)
                }
                MESSAGE_SENT->{
                    displayLoading(false)
                }
            }
        })
        viewModel.isMessageSent.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val intent = Intent(this, OrderSuccessful::class.java)
                        startActivity(intent)
                        finish()
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
        finish()
    }

    override fun onMenuClick() {

    }


    override fun onPaymentResultReceived(
        method: PaymentEnum,
        result: Boolean,
        message: String,
        paytmResponseModel: PaytmResponseModel?
    ) {
        viewModel.customerOrder = CustomerOrder(
            ShippingAddressId = viewModel.selectedAddressId.value,
            UserID = userId,
            Total = viewModel.grandTotal,
            Discount = viewModel.discountAmount,
            Shipping = viewModel.mShippingCharge,
            Tax = viewModel.tax,
            GrandTotal = viewModel.grandTotal,
            Promo = viewModel.discountCoupon,
        )
        if (result) {
            when (method) {
                PaymentEnum.WALLET -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Paid.id // paid
                    viewModel.customerOrder.WalletTypeId = WalletType.Wallet.id  // wallet
                    viewModel.customerOrder.PaymentMode = PaymentModes.Wallet.id   // wallet
                }
                PaymentEnum.PCASH -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Paid.id // paid
                    viewModel.customerOrder.WalletTypeId = WalletType.PCash.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.PCash.id
                }
                PaymentEnum.PAYTM -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Pending.id
                    viewModel.customerOrder.WalletTypeId = WalletType.OnlinePayment.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.Online.id

                    checkoutRepository.selectedPaymentMethod = PaymentEnum.PAYTM

                    if (paytmResponseModel != null) {
                        viewModel.paytmResponseModel = paytmResponseModel
                    }
                }
                PaymentEnum.COD -> {
                    viewModel.customerOrder.PaymentStatusId = PaymentStatus.Pending.id
                    viewModel.customerOrder.WalletTypeId = WalletType.CashOnDelivery.id
                    viewModel.customerOrder.PaymentMode = PaymentModes.CashOnDelivery.id
                }
            }
            viewModel.createCustomerOrder(viewModel.customerOrder)
        }
    }

    companion object{
        const val LOADING = 100
        const val SUCCESS = 101
        const val ERROR = 102
        const val CREATING_ORDER_NUMBER = 103
        const val ORDER_SUCCESSFUL = 104
        const val MESSAGE_SENT = 105
    }

}