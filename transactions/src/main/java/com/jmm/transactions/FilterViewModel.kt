package com.jmm.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.TransactionTypeModel
import com.jmm.model.UniversalFilterItemModel
import com.jmm.model.UniversalFilterModel
import com.jmm.repository.PaymentHistoryFilterRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FilterViewModel @Inject constructor(
        private val paymentHistoryFilterRepository: PaymentHistoryFilterRepository
) : ViewModel(){


    private val _paymentHistoryFilterList: MutableLiveData<Resource<List<UniversalFilterModel>>> = MutableLiveData()
    val paymentHistoryFilterList: LiveData<Resource<List<UniversalFilterModel>>> = _paymentHistoryFilterList

    fun getFilterList() {
        viewModelScope.launch {
            paymentHistoryFilterRepository
                    .fetchFilterList()
                    .onStart {
                        _paymentHistoryFilterList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _paymentHistoryFilterList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _paymentHistoryFilterList.postValue(Resource.Success(response))
                    }
        }

    }

    private val _transactionType = MutableLiveData<Resource<List<TransactionTypeModel>>>()
    val transactionType: LiveData<Resource<List<TransactionTypeModel>>> = _transactionType

    fun getTransactionType() {

        viewModelScope.launch {

            paymentHistoryFilterRepository
                    .getTransactionType()
                    .onStart {
                        _transactionType.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _transactionType.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _transactionType.postValue(Resource.Success(response))
                    }
        }

    }

    fun populateFilterList(transactionTypeList:MutableList<UniversalFilterItemModel>){
        paymentHistoryFilterRepository.populateFilterList(transactionTypeList)
    }

    fun updateFilterList(mList:MutableList<UniversalFilterModel>){
        paymentHistoryFilterRepository.updateFilterList(mList)
    }
}