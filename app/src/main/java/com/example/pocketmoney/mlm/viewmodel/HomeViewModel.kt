package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.TransactionModel
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.repository.WalletRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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


    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash


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


    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _pCash.postValue(Resource.Success(_balance))
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


    private val _sponsorName = MutableLiveData<Resource<String>>()
    val sponsorName: LiveData<Resource<String>> = _sponsorName

    fun getSponsorName(id: String) {
        viewModelScope.launch {
            accountRepository
                .getSponsorName(id)
                .onStart {
                    _sponsorName.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _sponsorName.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _sponsorName.postValue(Resource.Success(response))
                }
        }

    }

    private val _isAccountActive = MutableLiveData<Resource<Boolean>>()
    val isAccountActive: LiveData<Resource<Boolean>> = _isAccountActive

    fun checkIsAccountActive(id: String) {
        viewModelScope.launch {
            accountRepository
                .isUserAccountActive(id)
                .onStart {
                    _isAccountActive.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isAccountActive.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _isAccountActive.postValue(Resource.Success(response))
                }
        }

    }

}