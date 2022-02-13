package com.jmm.lock_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
):ViewModel(){

    val userId = userPreferencesRepository.userId.asLiveData()
    val userType = userPreferencesRepository.isActive.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val userPin = userPreferencesRepository.userPin.asLiveData()
    val isSecured = userPreferencesRepository.isSecured.asLiveData()

    fun updateSecurity(status:Boolean)=viewModelScope.launch {
        userPreferencesRepository.updateSecureStatus(status)
    }

    fun updateUserPin(pin:String)=viewModelScope.launch {
        userPreferencesRepository.updateUserPin(pin)
    }

    val error = MutableLiveData<String?>()

    var pinStatus = PinStatus.SettingNewPin
    var pin = ""
    var leftRetry = 4
}


enum class PinStatus{
    SettingNewPin,ConfirmNewPin,EnterPin
}