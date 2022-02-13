package com.jmm.onboarding

import androidx.lifecycle.*
import com.jmm.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingScreenViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    fun updateWelcomeStatus(status:Int)=viewModelScope.launch {
        userPreferencesRepository.updateWelcomeStatus(status)
    }


}