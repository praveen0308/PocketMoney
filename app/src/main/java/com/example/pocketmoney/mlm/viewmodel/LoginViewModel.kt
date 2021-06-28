package com.example.pocketmoney.mlm.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    fun updateUserName(userName:String)=viewModelScope.launch {
        userPreferencesRepository.updateUserName(userName)
    }

    fun updateUserRoleID(roleId:Int)=viewModelScope.launch {
        userPreferencesRepository.updateUserRoleId(roleId)
    }

    fun updateFirstName(firstName:String)=viewModelScope.launch {
        userPreferencesRepository.updateUserFirstName(firstName)
    }

    fun updateLastName(lastName:String)=viewModelScope.launch {
        userPreferencesRepository.updateUserLastName(lastName)
    }

    private val _userModel = MutableLiveData<Resource<UserModel>>()
    val userModel: LiveData<Resource<UserModel>> = _userModel

    fun doLogin(userId:String,password: String) {

        viewModelScope.launch {

            accountRepository
                    .doLogin(userId,password)
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
                            _userModel.postValue(Resource.Error(""))
                        }
                    }

        }
    }



}