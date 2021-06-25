package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.shopping.model.OrderListItem
import com.example.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import com.example.pocketmoney.shopping.repository.OrderRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
        private val orderRepository: OrderRepository
): ViewModel(){
    private val _orderList = MutableLiveData<Resource<List<OrderListItem>>>()
    val orderList : LiveData<Resource<List<OrderListItem>>> = _orderList

    fun getOrderList(userId:String) {
        viewModelScope.launch {
            orderRepository
                    .getOrderList(userId)
                    .onStart {
                        _orderList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _orderList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { list->
                        _orderList.postValue(Resource.Success(list))
                    }
        }

    }

    private val _orderDetails = MutableLiveData<Resource<ModelOrderDetails>>()
    val orderDetails : LiveData<Resource<ModelOrderDetails>> = _orderDetails

    fun getOrderDetails(orderNumber:String) {
        viewModelScope.launch {
            orderRepository
                    .getOrderDetails(orderNumber)
                    .onStart {
                        _orderDetails.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _orderDetails.postValue(Resource.Error(it))
                        }
                    }
                    .collect { details->
                        _orderDetails.postValue(Resource.Success(details))
                    }
        }

    }
}