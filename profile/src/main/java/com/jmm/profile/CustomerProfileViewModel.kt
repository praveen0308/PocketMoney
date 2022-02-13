package com.jmm.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.model.mlmModels.CustomerProfileModel
import com.jmm.repository.AccountRepository
import com.jmm.repository.CustomerRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()


    val pageState : MutableLiveData<CustomerProfilePageState> = MutableLiveData(CustomerProfilePageState.Idle)
    fun getUserProfileInfo(id: String) {
        viewModelScope.launch {
            customerRepository
                .getUserProfile(id)
                .onStart {
                    pageState.postValue(CustomerProfilePageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CustomerProfilePageState.Error(exception.identify()))
                    }
                }
                .collect { response->
                    pageState.postValue(CustomerProfilePageState.ReceivedProfileDetails(response))
                }
        }

    }


  /*  fun getCustomerBalanceWithAuth(userId:String,roleId:Int) {
        viewModelScope.launch {
            walletRepository
                .getCustomerBalanceWithAuth(userId, roleId)
                .onStart {
                    pageState.postValue(CustomerProfilePageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CustomerProfilePageState.Error(exception.identify()))
                    }
                }
                .collect { response->
                    pageState.postValue(CustomerProfilePageState.ReceivedAuthDetails(response))
                }
        }

    }
*/

}

sealed class CustomerProfilePageState{
    object Idle : CustomerProfilePageState()
    object Loading : CustomerProfilePageState()
    data class Error(val msg:String) : CustomerProfilePageState()
    data class Processing(val msg:String) : CustomerProfilePageState()
    data class ReceivedProfileDetails(val detail:CustomerProfileModel) : CustomerProfilePageState()
//    data class ReceivedAuthDetails(val detail:CustomerAuthBalanceResponse) : CustomerProfilePageState()

}