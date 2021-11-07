package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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