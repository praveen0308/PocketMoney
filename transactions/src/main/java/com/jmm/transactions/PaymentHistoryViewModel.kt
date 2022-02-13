package com.jmm.transactions

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
class PaymentHistoryViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    fun getWalletBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletBalance.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }

    private val _allTransactionHistory = MutableLiveData<Resource<List<TransactionModel>>>()
    val allTransactionHistory: LiveData<Resource<List<TransactionModel>>> = _allTransactionHistory


    fun getAllTransactionHistory(requestModel: CustomerRequestModel1) {

        viewModelScope.launch {

            walletRepository
                .getAllTransactionHistory(requestModel)
                .onStart {
                    _allTransactionHistory.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _allTransactionHistory.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _allTransactionHistory.postValue(Resource.Success(response))
                }
        }

    }

}