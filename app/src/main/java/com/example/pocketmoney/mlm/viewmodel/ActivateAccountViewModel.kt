package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.common.MailMessagingRepository
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.repository.*
import com.example.pocketmoney.utils.Resource
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
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
    val selectedMethod = MutableStateFlow(1)

    lateinit var paytmResponseModel: PaytmResponseModel

    private val _isValid = MutableLiveData<Resource<Int>>()
    val isValid: LiveData<Resource<Int>> = _isValid


    fun validateCustomerRegistration(mobile: String,pin:String,pinSerial:String) {
        viewModelScope.launch {
            customerRepository
                .validateCustomerRegistration(mobile,pin,pinSerial)
                .onStart {
                    _isValid.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isValid.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isValid.postValue(Resource.Success(response))
                    when(response){
                        1->_isValid.postValue(Resource.Error("Mobile no. already registered. Please register with new Mobile no"))
                        2->_isValid.postValue(Resource.Error("PIN or Serial no. does not exist, Please contact Admin"))
                        3->_isValid.postValue(Resource.Error("PIN or Serial no. already used, please contact Admin"))
                        4->_isValid.postValue(Resource.Error("PIN and Serial no. mismatched"))
                        else->{

                        }
                    }

                }
        }

    }

    private val _isActivationSuccessful = MutableLiveData<Resource<Int>>()
    val isActivationSuccessful: LiveData<Resource<Int>> = _isActivationSuccessful

    fun activateAccountUsingCoupon(userId: String,pin:String,pinSerial:String) {
        viewModelScope.launch {
            customerRepository
                .activateAccountUsingCoupon(userId,pinSerial,pin)
                .onStart {
                    _isActivationSuccessful.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isActivationSuccessful.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isActivationSuccessful.postValue(Resource.Success(response))
                }
        }

    }

    private val _isActivatedByPayment = MutableLiveData<Resource<Int>>()
    val isActivatedByPayment: LiveData<Resource<Int>> = _isActivatedByPayment

    fun activateAccountByPayment(userId: String,walletTypeId:Int) {
        viewModelScope.launch {
            customerRepository
                .onlineActivateAccount(userId,walletTypeId)
                .onStart {
                    _isActivatedByPayment.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isActivatedByPayment.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isActivatedByPayment.postValue(Resource.Success(response))
                }
        }

    }

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash


    fun getWalletBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletBalance.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }


    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }

    private val _isMessageSent = MutableLiveData<Resource<Boolean>>()
    val isMessageSent : LiveData<Resource<Boolean>> = _isMessageSent

    fun sendWhatsappMessage(mobileNumber:String,message:String) {

        viewModelScope.launch {
            mailMessagingRepository
                .sendWhatsappMessage(mobileNumber, message)
                .onStart {
                    _isMessageSent.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isMessageSent.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isMessageSent.postValue(Resource.Success(response))
                }
        }
    }

    private val _addPaymentTransResponse = MutableLiveData<Resource<String>>()
    val addPaymentTransResponse : LiveData<Resource<String>> = _addPaymentTransResponse

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {

        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPaymentTransResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addPaymentTransResponse.postValue(Resource.Success(response))
                }
        }
    }

    private val _isAccountActive = MutableLiveData<Resource<Boolean>>()
    val isAccountActive: LiveData<Resource<Boolean>> = _isAccountActive

    fun checkIsAccountActive(id: String) {
        viewModelScope.launch {
            accountRepository
                .isUserAccountActive(id)
                .onStart {
                    _isAccountActive.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isAccountActive.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _isAccountActive.postValue(Resource.Success(response))
                }
        }

    }

}