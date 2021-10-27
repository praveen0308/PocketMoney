package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import androidx.activity.viewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityActivateAccountBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.viewmodel.ActivateAccountViewModel
import com.example.pocketmoney.paymentgateway.PaymentPortal
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.WalletType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ActivateAccount : BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate),
    PaymentPortal.PaymentPortalCallback, ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<ActivateAccountViewModel>()

    private var selectedMethod = 1
    private var selectedPaymentMethod = PaymentEnum.WALLET
    private var userId = ""
    private var roleId = 0
    private val activationCharge = 300.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivateAccount.setApplicationToolbarListener(this)
        binding.btnActivate.setOnClickListener {
            when(selectedMethod){
                1->{
                    val sheet = ActivateAccUsingCoupon()
                    sheet.show(supportFragmentManager,sheet.tag)
                }
                2->{
                    val sheet = PaymentPortal(this,activationCharge)
                    sheet.show(supportFragmentManager,sheet.tag)
                }
            }
        }

        binding.rgPaymentChoices.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_coupon->{
                    selectedMethod = 1
                }
                R.id.rb_payment->{
                    selectedMethod = 2
                }
            }
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })
        viewModel.userRoleID.observe(this,{
            roleId = it
        })


        viewModel.isActivationSuccessful.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if(selectedPaymentMethod == PaymentEnum.PAYTM){
                            viewModel.addPaymentTransactionDetail(
                                PaymentGatewayTransactionModel(
                                    UserId = userId,
                                    OrderId = viewModel.paytmResponseModel.ORDERID,
                                    ReferenceTransactionId = viewModel.paytmResponseModel.ORDERID,
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
                        }else{
                            viewModel.checkIsAccountActive(userId)
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


        viewModel.isAccountActive.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            val msg= "Congratulations! You have upgraded your pocketmoney account to PRO account. Start using the pocketmoney services and enjoy the unlimited benefits,Click https://www.pocketmoney.net.in"
                            viewModel.sendWhatsappMessage(userId,msg)
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
                        if (viewModel.paytmResponseModel.STATUS == "SUCCESS"){
                            Timber.d("Payment Gateway response was successful.")
                            viewModel.checkIsAccountActive(userId)

                        }else if (viewModel.paytmResponseModel.STATUS == "FAILED" || viewModel.paytmResponseModel.STATUS == "FAILURE"){
                            Timber.d("Payment Gateway response was failed.")
                            showToast("Payment failed!!!")
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

    }

    override fun onPaymentResultReceived(
        method: PaymentEnum,
        result: Boolean,
        message: String,
        paytmResponseModel: PaytmResponseModel?
    ) {
        paytmResponseModel?.let {
            viewModel.paytmResponseModel = it
        }
        selectedPaymentMethod = method
        if (result){
            when(selectedPaymentMethod){
                PaymentEnum.WALLET-> viewModel.activateAccountByPayment(userId,WalletType.Wallet.id)
                PaymentEnum.PCASH-> viewModel.activateAccountByPayment(userId,WalletType.PCash.id)
                PaymentEnum.PAYTM->viewModel.activateAccountByPayment(userId,WalletType.OnlinePayment.id)
            }
        }
        else{
            showToast("Cancelled !!")
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }


}