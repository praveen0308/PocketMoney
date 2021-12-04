package com.sampurna.pocketmoney.mlm.ui.forgotpassword

import androidx.lifecycle.*
import com.sampurna.pocketmoney.common.MailMessagingRepository
import com.sampurna.pocketmoney.common.SMSResponseModel
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.CustomerRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository,
    private val accountRepository: AccountRepository,
    private val mailMessagingRepository: MailMessagingRepository
) :ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    var enterUserId = ""
    var generatedOtp = ""
    var isNotified = false

    fun clearUserInfo() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserInfo()
        }

    }

    private val _resetPasswordResponse = MutableLiveData<Resource<Boolean>>()
    val resetPasswordResponse: LiveData<Resource<Boolean>> = _resetPasswordResponse

    fun resetPassword(userId: String,otp:String) {
        viewModelScope.launch {
            accountRepository
                .resetPassword(userId,0,otp,"FGTPWD")
                .onStart {
                    _resetPasswordResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _resetPasswordResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _resetPasswordResponse.postValue(Resource.Success(response))
                }
        }
    }

    private val _confirmOtpResponse = MutableLiveData<Resource<Boolean>>()
    val confirmOtpResponse: LiveData<Resource<Boolean>> = _confirmOtpResponse

    fun confirmOtp(userId: String,otp:String) {
        viewModelScope.launch {
            accountRepository
                .resetPassword(userId,0,otp,"VERIFYOTP")
                .onStart {
                    _confirmOtpResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _confirmOtpResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _confirmOtpResponse.postValue(Resource.Success(response))
                }
        }
    }


    private val _changePasswordResponse = MutableLiveData<Resource<Boolean>>()
    val changePasswordResponse: LiveData<Resource<Boolean>> = _changePasswordResponse

    fun changePassword(userId: String,password:String) {
        viewModelScope.launch {
            accountRepository
                .resetPassword(userId,0,password,"RESETPWD")
                .onStart {
                    _changePasswordResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _changePasswordResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _changePasswordResponse.postValue(Resource.Success(response))
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
                        _isMessageSent.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error caused by >>>> sendWhatsappMessage")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { response ->
                    if (response) {
                        _isMessageSent.postValue(Resource.Success(response))
                        sendOTPSms(enterUserId, generatedOtp)
                        isNotified = true
                    } else {
                        _isMessageSent.postValue(Resource.Error("Something went wrong !!!"))
                    }

                }
        }
    }


    private val _sendSmsResponse = MutableLiveData<Resource<SMSResponseModel>>()
    val sendSmsResponse: LiveData<Resource<SMSResponseModel>> = _sendSmsResponse

    fun sendOTPSms(
        mobileNo: String,
        otp: String
    ) {
        viewModelScope.launch {
            mailMessagingRepository
                .sendOtpSMS(mobileNo, otp)
                .onStart {
                    _sendSmsResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _sendSmsResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error caused by >>>> sendSmsOfTransaction")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    _sendSmsResponse.postValue(Resource.Success(it))
                }
        }
    }

}