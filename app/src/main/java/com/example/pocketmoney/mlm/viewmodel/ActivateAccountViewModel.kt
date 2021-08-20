package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
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
    private val customerRepository: CustomerRepository
): ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    val selectedMethod = MutableStateFlow(1)

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
}