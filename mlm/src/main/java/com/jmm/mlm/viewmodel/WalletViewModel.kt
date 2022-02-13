package com.jmm.mlm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.TransactionModel
import com.jmm.model.mlmModels.CustomerRequestModel1
import com.jmm.repository.WalletRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
        private val walletRepository: WalletRepository
) : ViewModel() {

//    private val _walletBalance = MutableLiveData<DataState<Double>>()
//    val walletBalance: LiveData<DataState<Double>> = _walletBalance
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


