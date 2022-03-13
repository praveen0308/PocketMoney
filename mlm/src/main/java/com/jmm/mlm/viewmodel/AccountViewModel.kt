package com.jmm.mlm.viewmodel

import androidx.lifecycle.*
import com.jmm.model.CustomerDashboardDataModel
import com.jmm.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
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

    val pageState: MutableLiveData<AccountPageState> = MutableLiveData(AccountPageState.Idle)

    fun clearUserInfo() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserInfo()
        }

    }

    private val _accountData: MutableLiveData<IResource<CustomerDashboardDataModel>> =
        MutableLiveData<IResource<CustomerDashboardDataModel>>()
    val accountData: LiveData<IResource<CustomerDashboardDataModel>> = _accountData
    fun getDashboardData(userId: String, roleId: Int) {
        viewModelScope.launch {
            accountRepository
                .getDashboardData(userId, roleId)
                .onStart {
                    _accountData.postValue(IResource.Loading())
                }
                .catch {exception->
                    _accountData.postValue(IResource.Error(exception))
                }
                .collect {
                    _accountData.postValue(IResource.Success(it.data!!))
                }
        }

    }


}


sealed class AccountPageState {
    object Idle : AccountPageState()
    object Loading : AccountPageState()

    data class Error(val msg: String) : AccountPageState()
    data class Processing(val msg: String) : AccountPageState()
    data class ReceivedData(val data: CustomerDashboardDataModel) : AccountPageState()

}