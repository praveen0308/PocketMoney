package com.jmm.core

import androidx.lifecycle.*
import com.jmm.repository.AccountRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val welcomeStatus = userPreferencesRepository.welcomeStatus.asLiveData()

    fun updateSponsorId(sponsorId: String) = viewModelScope.launch {
        userPreferencesRepository.updateSponsorId(sponsorId)
    }

    fun updateSponsorName(sponsorName: String) = viewModelScope.launch {
        userPreferencesRepository.updateSponsorName(sponsorName)
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


}