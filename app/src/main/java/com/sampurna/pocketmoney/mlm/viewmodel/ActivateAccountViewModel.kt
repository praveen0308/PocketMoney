package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sampurna.pocketmoney.common.MailMessagingRepository
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.mlm.repository.*
import com.sampurna.pocketmoney.utils.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ActivateAccountViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository,
    private val mailMessagingRepository: MailMessagingRepository,
    private val paytmRepository: PaytmRepository,
    private val accountRepository: AccountRepository

): ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    fun updateUserType(status: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateUserType(status)
    }

    fun clearUserInfo() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserInfo()
        }

    }

    val activationCharge = 300.0
    lateinit var paytmResponseModel: PaytmResponseModel

    val pageState: MutableLiveData<ActivateAccountPageState> =
        MutableLiveData(ActivateAccountPageState.Idle)

    val couponPageState: MutableLiveData<ActivateUsingCouponPageState> =
        MutableLiveData(ActivateUsingCouponPageState.Idle)

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.Loading(true))
                }
                .catch { exception ->
                    pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                    Timber.e("Error caused by >>> getWalletBalance")
                    Timber.e("Exception >>> ${exception.message}")
                }
                .collect { _balance ->
                    if (_balance < activationCharge) pageState.postValue(ActivateAccountPageState.InsufficientBalance)
                    else pageState.postValue(ActivateAccountPageState.GotWalletBalance(_balance))
                }
        }

    }

    fun getPCashBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.Loading(true))
                }
                .catch { exception ->
                    pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                    Timber.e("Error caused by >>> getPCashBalance")
                    Timber.e("Exception >>> ${exception.message}")
                }
                .collect { _balance ->
                    if (_balance < activationCharge) pageState.postValue(ActivateAccountPageState.InsufficientBalance)
                    else pageState.postValue(ActivateAccountPageState.GotPCashBalance(_balance))
                }
        }

    }

    fun validateCustomerRegistration(mobile: String, pin: String, pinSerial: String) {
        viewModelScope.launch {
            customerRepository
                .validateCustomerRegistration(mobile, pin, pinSerial)
                .onStart {
                    couponPageState.postValue(ActivateUsingCouponPageState.ValidatingCustomerRegistration)
                }
                .catch { exception ->
                    exception.message?.let {
                        couponPageState.postValue(ActivateUsingCouponPageState.Error(exception.identify()))
                        Timber.e("Error Caused by >>> validateCustomerRegistration")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->

                    when (response) {
                        1 -> couponPageState.postValue(ActivateUsingCouponPageState.Error("Mobile no. already registered. Please register with new Mobile no"))
                        2 -> couponPageState.postValue(ActivateUsingCouponPageState.Error("PIN or Serial no. does not exist, Please contact Admin"))
                        3 -> couponPageState.postValue(ActivateUsingCouponPageState.Error("PIN or Serial no. already used, please contact Admin"))
                        4 -> couponPageState.postValue(ActivateUsingCouponPageState.Error("PIN and Serial no. mismatched"))
                        else -> {
                            couponPageState.postValue(ActivateUsingCouponPageState.Valid)
                            pageState.postValue(
                                ActivateAccountPageState.CouponValidated(
                                    pin,
                                    pinSerial
                                )
                            )
                        }
                    }

                }
        }

    }


    fun activateAccountUsingCoupon(userId: String,pin:String,pinSerial:String) {
        viewModelScope.launch {
            customerRepository
                .activateAccountUsingCoupon(userId,pinSerial,pin)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.RequestingActivation)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                        Timber.e("Error Caused by >>> activateAccountUsingCoupon")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    pageState.postValue(ActivateAccountPageState.ActivationDone(response, "coupon"))
                }
        }

    }

    fun activateAccountByPayment(userId: String,walletTypeId:Int) {
        viewModelScope.launch {
            customerRepository
                .onlineActivateAccount(userId,walletTypeId)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.RequestingActivation)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                        Timber.e("Error Caused by >>> activateAccountUsingCoupon")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    pageState.postValue(
                        ActivateAccountPageState.ActivationDone(
                            response,
                            "payment"
                        )
                    )
                }
        }

    }

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {
        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.SendingPaymentTransDetail)

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                        Timber.e("Error Caused by >>> addPaymentTransactionDetail")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response ->
                    pageState.postValue(ActivateAccountPageState.SubmittedPaymentTransDetail)
                }
        }
    }

    fun checkIsAccountActive(id: String) {
        viewModelScope.launch {
            accountRepository
                .isUserAccountActive(id)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.CheckingActivationState)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                        Timber.e("Error Caused by >>> checkIsAccountActive")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response ->
                    pageState.postValue(ActivateAccountPageState.GotActivationState(response))
                }
        }
    }

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.InitiatingTransaction)
                }
                .catch { exception ->
                    pageState.postValue(ActivateAccountPageState.Error(exception.identify()))
                    Timber.e("Error Caused by >>> initiateTransactionApi")
                    Timber.e("Exception >>> ${exception.message}")

                }
                .collect { response->
                    pageState.postValue(ActivateAccountPageState.ReceivedCheckSum(response))
                }
        }

    }


    fun sendWhatsappMessage(mobileNumber: String, message: String) {
        viewModelScope.launch {
            mailMessagingRepository
                .sendWhatsappMessage(mobileNumber, message)
                .onStart {
                    pageState.postValue(ActivateAccountPageState.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ActivateAccountPageState.MessageFailed)
                        Timber.e("Error Caused by >>> sendWhatsappMessage")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response ->
                    pageState.postValue(ActivateAccountPageState.MessageSent)
                }
        }
    }


}

sealed class ActivateAccountPageState {

    object Idle : ActivateAccountPageState()
    object NoInternet : ActivateAccountPageState()
    data class Loading(val isLoading: Boolean) : ActivateAccountPageState()
    data class GotWalletBalance(val balance: Double) : ActivateAccountPageState()
    data class GotPCashBalance(val balance: Double) : ActivateAccountPageState()
    object InsufficientBalance : ActivateAccountPageState()

    object InitiatingTransaction : ActivateAccountPageState()
    data class ReceivedCheckSum(val token: String) : ActivateAccountPageState()
    object RequestingGateway : ActivateAccountPageState()
    object WaitingForGatewayResponse : ActivateAccountPageState()
    data class ReceivedGatewayResponse(val response: PaytmResponseModel) :
        ActivateAccountPageState()

    object SendingPaymentTransDetail : ActivateAccountPageState()
    object SubmittedPaymentTransDetail : ActivateAccountPageState()

    object RequestingActivation : ActivateAccountPageState()
    data class ActivationDone(val response: Int, val by: String) : ActivateAccountPageState()

    object CheckingActivationState : ActivateAccountPageState()
    data class GotActivationState(val isActive: Boolean) : ActivateAccountPageState()

    data class CouponValidated(val pinNo: String, val pinSerial: String) :
        ActivateAccountPageState()

    data class Error(val msg: String) : ActivateAccountPageState()

    object MessageSent : ActivateAccountPageState()
    object MessageFailed : ActivateAccountPageState()

}

sealed class ActivateUsingCouponPageState {
    object Idle : ActivateUsingCouponPageState()
    object Loading : ActivateUsingCouponPageState()
    data class Error(val msg: String) : ActivateUsingCouponPageState()
    object ValidatingCustomerRegistration : ActivateUsingCouponPageState()
    object Invalid : ActivateUsingCouponPageState()
    object Valid : ActivateUsingCouponPageState()
}