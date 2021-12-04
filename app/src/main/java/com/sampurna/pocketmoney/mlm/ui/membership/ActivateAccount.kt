package com.sampurna.pocketmoney.mlm.ui.membership

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.jmm.brsap.dialog_builder.DialogType
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.common.PaymentMethods
import com.sampurna.pocketmoney.databinding.ActivityActivateAccountBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.mlm.ui.MainActivity
import com.sampurna.pocketmoney.mlm.viewmodel.ActivateAccountPageState
import com.sampurna.pocketmoney.mlm.viewmodel.ActivateAccountViewModel
import com.sampurna.pocketmoney.utils.*
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import com.sampurna.pocketmoney.utils.myEnums.WalletType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ActivateAccount :
    BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener,
    PaymentMethods.PaymentMethodsInterface, PaytmPaymentTransactionCallback {

    private val viewModel by viewModels<ActivateAccountViewModel>()

    private var selectedMethod = 1
    private var selectedPaymentMethod = PaymentEnum.WALLET
    private var userId = ""
    private var roleId = 0

    private var gatewayOrderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivateAccount.setApplicationToolbarListener(this)
        binding.btnActivate.setOnClickListener {

            when (selectedMethod) {
                1 -> {
                    val sheet = ActivateAccUsingCoupon()
                    sheet.show(supportFragmentManager, sheet.tag)
                }
                2-> {
                    val sheet = PaymentMethods(this)
                    sheet.show(supportFragmentManager, sheet.tag)
//                    val sheet = PaymentPortal(this,activationCharge)
//                    sheet.show(supportFragmentManager,sheet.tag)
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
        viewModel.userId.observe(this, {
            userId = it
        })
        viewModel.userRoleID.observe(this, {
            roleId = it
        })

        viewModel.pageState.observe(this, { state ->
            displayLoading(false)
            when (state) {
                is ActivateAccountPageState.Idle -> {
                }
                is ActivateAccountPageState.Loading -> {
                    displayLoading(state.isLoading)
                }
                is ActivateAccountPageState.Error -> {
                    showToast(state.msg)
                }

                is ActivateAccountPageState.InsufficientBalance -> {
                    showToast("Insufficient balance!!!")
                }
                is ActivateAccountPageState.GotWalletBalance -> {
                    viewModel.activateAccountByPayment(userId, WalletType.Wallet.id)
                }
                is ActivateAccountPageState.GotPCashBalance -> {
                    viewModel.activateAccountByPayment(userId, WalletType.PCash.id)
                }

                is ActivateAccountPageState.InitiatingTransaction -> {
                    showLoadingDialog("Initiating transaction")
                }
                is ActivateAccountPageState.ReceivedCheckSum -> {
                    val paytmOrder = PaytmOrder(
                        gatewayOrderId,
                        Constants.P_MERCHANT_ID,
                        state.token,
                        viewModel.activationCharge.toString(),
                        Constants.PAYTM_CALLBACK_URL + gatewayOrderId
                    )
                    processPaytmTransaction(paytmOrder)
                }


                is ActivateAccountPageState.RequestingGateway -> {
                    showLoadingDialog("Please wait!!! Requesting gateway...")
                }
                is ActivateAccountPageState.WaitingForGatewayResponse -> {
                    showLoadingDialog("Waiting...")
                }
                is ActivateAccountPageState.ReceivedGatewayResponse -> {
                    when (viewModel.paytmResponseModel.STATUS) {
                        "SUCCESS", "FAILURE" -> {
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
                            showActionDialog(this, DialogType.ERROR, "Oops!",
                                "Something went wrong!! Contact admin...", mListener = {
                                    finish()
                                })
                        }
                    }
                }
                is ActivateAccountPageState.SendingPaymentTransDetail -> {
                    showLoadingDialog("Processing....")
                }
                is ActivateAccountPageState.SubmittedPaymentTransDetail -> {
                    if (viewModel.paytmResponseModel.STATUS == "SUCCESS") {
                        Timber.d("Payment Gateway response was successful.")
                        viewModel.activateAccountByPayment(userId, WalletType.OnlinePayment.id)

                    } else if (viewModel.paytmResponseModel.STATUS == "FAILED" || viewModel.paytmResponseModel.STATUS == "FAILURE") {
                        Timber.d("Payment Gateway response was failed.")
                        showActionDialog(this, DialogType.ERROR, "Oops!",
                            "Payment failed!!!", mListener = {

                            })

                        //exit
                    }

                }

                is ActivateAccountPageState.RequestingActivation -> {
                    showLoadingDialog("Please wait!!! Activating...")
                }
                is ActivateAccountPageState.ActivationDone -> {
                    viewModel.checkIsAccountActive(userId)
                }


                is ActivateAccountPageState.CheckingActivationState -> {
                    showLoadingDialog("Validating...")
                }
                is ActivateAccountPageState.GotActivationState -> {
                    if (state.isActive) {
                        val msg =
                            "Congratulations! You have upgraded your pocketmoney account to PRO account. Start using the pocketmoney services and enjoy the unlimited benefits,Click https://www.pocketmoney.net.in"
                        viewModel.sendWhatsappMessage(userId, msg)

                    } else {
                        showToast("Unfortunately activation was failed!!!")
                        //exit
                    }
                }

                is ActivateAccountPageState.MessageSent -> {
                    showToast("Activation done successfully!!!")

                    viewModel.clearUserInfo()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is ActivateAccountPageState.MessageFailed -> {
                    showToast("Activation done successfully!!!")
                    viewModel.clearUserInfo()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is ActivateAccountPageState.CouponValidated -> {
                    viewModel.activateAccountUsingCoupon(userId, state.pinNo, state.pinSerial)
                }

            }
        })

    }


    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {
        when (method) {
            PaymentEnum.WALLET -> viewModel.getWalletBalance(userId, roleId)
            PaymentEnum.PCASH -> viewModel.getPCashBalance(userId, roleId)
            PaymentEnum.PAYTM -> startPayment()
        }
    }

    private fun startPayment() {
        gatewayOrderId = createRandomOrderId()
        viewModel.initiateTransactionApi(
            PaytmRequestData(
                account = gatewayOrderId,
                amount = viewModel.activationCharge.toString(),
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
            transactionManager.startTransaction(this, 3)
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
            viewModel.pageState.postValue(ActivateAccountPageState.ReceivedGatewayResponse(viewModel.paytmResponseModel))
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


}