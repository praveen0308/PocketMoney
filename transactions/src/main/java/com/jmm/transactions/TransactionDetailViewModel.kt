package com.jmm.transactions

import androidx.lifecycle.*
import com.jmm.model.TransactionDetailModel
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
class TransactionDetailViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val walletRepository: WalletRepository
) : ViewModel(){
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    private val _transactionDetailResponse = MutableLiveData<Resource<TransactionDetailModel>>()
    val transactionDetailResponse: LiveData<Resource<TransactionDetailModel>> = _transactionDetailResponse

    fun viewTransactionDetails(transactionId: String) {
        viewModelScope.launch {
            walletRepository
                .viewTransactionDetail(transactionId)
                .onStart {
                    _transactionDetailResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _transactionDetailResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _transactionDetailResponse.postValue(Resource.Success(response))
                }
        }
    }

}