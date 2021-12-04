package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sampurna.pocketmoney.mlm.model.UserModel
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository

):ViewModel(){

    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    fun updateWelcomeStatus(status:Int)=viewModelScope.launch {
        userPreferencesRepository.updateWelcomeStatus(status)
    }

    fun updateLoginId(loginId:Int)=viewModelScope.launch {
        userPreferencesRepository.updateLoginId(loginId)
    }

    fun updateUserId(userId:String)=viewModelScope.launch {
        userPreferencesRepository.updateUserId(userId)
    }

    fun updateUserName(userName: String) = viewModelScope.launch {
        userPreferencesRepository.updateUserName(userName)
    }

    fun updateUserRoleID(roleId: Int) = viewModelScope.launch {
        userPreferencesRepository.updateUserRoleId(roleId)
    }

    fun updateUserStatus(status: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateStatus(status)
    }

    fun updateUserType(isActive: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateUserType(isActive)
    }


    val pageState: MutableLiveData<LoginPageState> = MutableLiveData(LoginPageState.Idle)

    fun doLogin(userId: String, password: String) {

        viewModelScope.launch {

            accountRepository
                .doLogin(userId, password)
                .onStart {
                    pageState.postValue(LoginPageState.Loading(true))
                }
                    .catch { exception ->
                        exception.message?.let {
                            pageState.postValue(LoginPageState.Error(exception.identify()))
                            Timber.e("Error caused by >>> doLogin")
                            Timber.e("Exception >>> ${exception.message}")
                        }
                    }
                    .collect {
                        if (it != null) {
                            pageState.postValue(LoginPageState.LoginSuccessful(it))
                        } else {
                            pageState.postValue(LoginPageState.Error("Invalid username or password!!!"))

                        }
                    }
        }
    }

    fun checkIsAccountActive(id: String) {
        viewModelScope.launch {
            accountRepository
                .isUserAccountActive(id)
                .onStart {
                    pageState.postValue(LoginPageState.Loading(true))
                }
                .catch { exception ->
                    pageState.postValue(LoginPageState.Error(exception.identify()))
                    Timber.e("Error caused by >>> checkIsAccountActive")
                    Timber.e("Exception >>> ${exception.message}")
                }
                .collect { response ->
                    pageState.postValue(LoginPageState.GotAccountStatus(response))
                }
        }
    }

}

sealed class LoginPageState {
    object Idle : LoginPageState()
    data class Loading(val isLoading: Boolean) : LoginPageState()
    data class Error(val msg: String) : LoginPageState()
    data class LoginSuccessful(val userModel: UserModel) : LoginPageState()
    data class GotAccountStatus(val status: Boolean) : LoginPageState()
}