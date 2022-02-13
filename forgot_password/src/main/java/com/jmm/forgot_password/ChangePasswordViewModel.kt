package com.jmm.forgot_password

import androidx.lifecycle.*
import com.jmm.model.UserModel
import com.jmm.repository.AccountRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    private val _userModel = MutableLiveData<Resource<UserModel>>()
    val userModel: LiveData<Resource<UserModel>> = _userModel

    fun doLogin(userId: String, password: String) {
        viewModelScope.launch {
            accountRepository
                .doLogin(userId, password)
                .onStart {
                    _userModel.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _userModel.postValue(Resource.Error("Something went wrong !!"))
                        Timber.e(exception)
                    }
                }
                .collect {
                    if (it != null) {
                        _userModel.postValue(Resource.Success(it))
                    } else {
                        _userModel.postValue(Resource.Error("You've entered wrong old password. Try again with correct one."))
                    }
                }
        }
    }

    private val _changePasswordResponse = MutableLiveData<Resource<Boolean>>()
    val changePasswordResponse: LiveData<Resource<Boolean>> = _changePasswordResponse

    fun changePassword(userId: String, password: String) {
        viewModelScope.launch {
            accountRepository
                .resetPassword(userId, 0, password, "RESETPWD")
                .onStart {
                    _changePasswordResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _changePasswordResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _changePasswordResponse.postValue(Resource.Success(response))
                }
        }
    }

}