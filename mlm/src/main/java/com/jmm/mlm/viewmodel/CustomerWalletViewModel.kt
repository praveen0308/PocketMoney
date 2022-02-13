package com.jmm.mlm.viewmodel

import androidx.lifecycle.*
import com.jmm.model.TransactionModel
import com.jmm.model.mlmModels.CustomerRequestModel1
import com.jmm.repository.AccountRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerWalletViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    private val _transactionHistory = MutableLiveData<Resource<List<TransactionModel>>>()
    val transactionHistory: LiveData<Resource<List<TransactionModel>>> = _transactionHistory

    fun getTransactionHistory(requestModel1: CustomerRequestModel1) {

        viewModelScope.launch {

            walletRepository
                .getTransactionHistory(requestModel1)
                .onStart {
                    _transactionHistory.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _transactionHistory.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _transactionHistory.postValue(Resource.Success(response))
                }
        }

    }

}