package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.sampurna.pocketmoney.common.MailMessagingRepository
import com.sampurna.pocketmoney.common.SMSResponseModel
import com.sampurna.pocketmoney.mlm.model.ModelCustomerDetail
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val walletRepository: WalletRepository,
    private val mailMessagingRepository: MailMessagingRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val userSponsorId = userPreferencesRepository.sponsorId.asLiveData()
    val userSponsorName = userPreferencesRepository.sponsorName.asLiveData()


    val pageState: MutableLiveData<RegisterPageState> = MutableLiveData(RegisterPageState.Idle)

    fun clearUserInfo() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserInfo()
        }

    }

    private val _isAccountDuplicate = MutableLiveData<Resource<Boolean>>()
    val isAccountDuplicate: LiveData<Resource<Boolean>> = _isAccountDuplicate


    fun checkAccountAlreadyExist(userId: String) {

        viewModelScope.launch {

            accountRepository
                .checkAccountAlreadyExist(userId)
                .onStart {
                    _isAccountDuplicate.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isAccountDuplicate.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _isAccountDuplicate.postValue(Resource.Success(response))
                }
        }

    }

    private val _isSuccessfullyRegistered = MutableLiveData<Resource<Boolean>>()
    val isSuccessfullyRegistered: LiveData<Resource<Boolean>> = _isSuccessfullyRegistered

    fun registerUser(customerDetail: ModelCustomerDetail) {
        viewModelScope.launch {
            accountRepository
                .registerUser(customerDetail)
                .onStart {
                    _isSuccessfullyRegistered.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isSuccessfullyRegistered.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _isSuccessfullyRegistered.postValue(Resource.Success(response))
                }
        }

    }

    private val _sponsorName = MutableLiveData<Resource<String>>()
    val sponsorName: LiveData<Resource<String>> = _sponsorName

    fun getSponsorName(id: String) {
        viewModelScope.launch {
            accountRepository
                .getSponsorName(id)
                .onStart {
                    _sponsorName.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _sponsorName.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _sponsorName.postValue(Resource.Success(response))
                }
        }

    }



    private val _dashboardData = MutableLiveData<Resource<JsonObject>>()
    val dashboardData: LiveData<Resource<JsonObject>> = _dashboardData


    fun getDashboardData(userId: String, roleId: Int) {

        viewModelScope.launch {

            accountRepository
                .getDashboardData(userId, roleId)
                .onStart {
                    _dashboardData.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _dashboardData.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _dashboardData.postValue(Resource.Success(response))
                }
        }

    }

    private val _smsResponse = MutableLiveData<Resource<SMSResponseModel>>()
    val smsResponse: LiveData<Resource<SMSResponseModel>> = _smsResponse

    fun sendRegistrationSms(mobileNo: String, userId: String, password: String) {
        viewModelScope.launch {
            mailMessagingRepository
                .sendRegistrationMessage(mobileNo, userId, password)
                .onStart {
                    _smsResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _smsResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error caused by >>>> sendRegistrationSms")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    _smsResponse.postValue(Resource.Success(it))
                }
        }
    }


}


sealed class RegisterPageState {
    object Idle : RegisterPageState()
    object Loading : RegisterPageState()

}