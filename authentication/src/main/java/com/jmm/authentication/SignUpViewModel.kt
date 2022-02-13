package com.jmm.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.model.ModelCustomerDetail
import com.jmm.repository.AccountRepository
import com.jmm.repository.MailMessagingRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
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


    fun checkAccountAlreadyExist(userId: String) {
        viewModelScope.launch {
            accountRepository
                .checkAccountAlreadyExist(userId)
                .onStart {
                    pageState.postValue(RegisterPageState.Processing("Validating..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(RegisterPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> checkAccountAlreadyExist")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response ->
                    pageState.postValue(RegisterPageState.AccountStatus(response))
                }
        }

    }


    fun registerUser(customerDetail: ModelCustomerDetail) {
        viewModelScope.launch {
            accountRepository
                .registerUser(customerDetail)
                .onStart {
                    pageState.postValue(RegisterPageState.Processing("Registering..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(RegisterPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> registerUser")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response ->
                    pageState.postValue(RegisterPageState.OnRegistrationComplete(response))
                }
        }

    }


    fun getSponsorName(id: String) {
        viewModelScope.launch {
            accountRepository
                .getSponsorName(id)
                .onStart {
                    pageState.postValue(RegisterPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(RegisterPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> getSponsorName")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response ->
                    pageState.postValue(RegisterPageState.ReceivedSponsorName(response))
                }
        }

    }


    fun sendRegistrationSms(mobileNo: String, userId: String, password: String) {
        viewModelScope.launch {
            mailMessagingRepository
                .sendRegistrationMessage(mobileNo, userId, password)
                .onStart {

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(RegisterPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> sendRegistrationSms")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    pageState.postValue(RegisterPageState.MessageSent)
                }
        }
    }


}


sealed class RegisterPageState {
    object Idle : RegisterPageState()
    object Loading : RegisterPageState()

    data class Error(val msg:String):RegisterPageState()
    data class Processing(val msg:String):RegisterPageState()
    data class ReceivedSponsorName(val name:String): RegisterPageState()

    data class AccountStatus(val status:Boolean):RegisterPageState()

    data class OnRegistrationComplete(val customerDetail: ModelCustomerDetail):RegisterPageState()
    object MessageSent : RegisterPageState()
}