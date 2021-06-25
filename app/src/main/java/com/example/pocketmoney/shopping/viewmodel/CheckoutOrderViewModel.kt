package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.repository.PaytmRepository
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutOrderViewModel @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    private val paytmRepository: PaytmRepository
): ViewModel() {

    private val _activeStep = MutableLiveData<Int>()
    val activeStep: LiveData<Int> = _activeStep

    fun setActiveStep(step:Int){
        _activeStep.postValue(step)
    }


    private val _orderStatus = MutableLiveData<Resource<Boolean>>()
    val orderStatus : LiveData<Resource<Boolean>> = _orderStatus

    fun createCustomerOrder(customerOrder: CustomerOrder) {

        viewModelScope.launch {

            checkoutRepository
                .createCustomerOrder(customerOrder)
                .onStart {
                    _orderStatus.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _orderStatus.postValue(Resource.Error(it))
                    }
                }
                .collect { status->
                    _orderStatus.postValue(Resource.Success(status))
                }
        }

    }


    private val _checkSum = MutableLiveData<Resource<String>>()
    val checkSum : LiveData<Resource<String>> = _checkSum

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _checkSum.postValue(Resource.Success(response))
                }
        }

    }

}