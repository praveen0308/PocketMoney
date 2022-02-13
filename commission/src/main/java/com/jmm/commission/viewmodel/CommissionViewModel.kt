package com.jmm.commission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.mlmModels.CommissionHistoryModel
import com.jmm.model.mlmModels.GrowthComissionRequestModel
import com.jmm.model.mlmModels.GrowthCommissionResponse
import com.jmm.repository.CustomerRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CommissionViewModel @Inject constructor(
        private val customerRepository: CustomerRepository
):ViewModel(){


    private val _growthCommission = MutableLiveData<Resource<GrowthCommissionResponse>>()
    val growthCommission: LiveData<Resource<GrowthCommissionResponse>> = _growthCommission


    fun getGrowthCommission(requestModel: GrowthComissionRequestModel) {

        viewModelScope.launch {

            customerRepository
                    .getGrowthCommission(requestModel)
                    .onStart {
                        _growthCommission.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _growthCommission.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _growthCommission.postValue(Resource.Success(response))
                    }
        }

    }

    private val _customerDirectCommission = MutableLiveData<Resource<List<CommissionHistoryModel>>>()
    val customerDirectCommission: LiveData<Resource<List<CommissionHistoryModel>>> = _customerDirectCommission


    fun getCustomerDirectCommission(requestModel: GrowthComissionRequestModel) {

        viewModelScope.launch {

            customerRepository
                    .getCustomerDirectCommission(requestModel)
                    .onStart {
                        _customerDirectCommission.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _customerDirectCommission.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _customerDirectCommission.postValue(Resource.Success(response))
                    }
        }

    }


    private val _customerUpdateCommission = MutableLiveData<Resource<List<CommissionHistoryModel>>>()
    val customerUpdateCommission: LiveData<Resource<List<CommissionHistoryModel>>> = _customerUpdateCommission


    fun getCustomerUpdateCommission(requestModel: GrowthComissionRequestModel) {

        viewModelScope.launch {

            customerRepository
                    .getCustomerUpdateCommission(requestModel)
                    .onStart {
                        _customerUpdateCommission.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _customerUpdateCommission.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _customerUpdateCommission.postValue(Resource.Success(response))
                    }
        }

    }

    private val _customerServiceCommission = MutableLiveData<Resource<List<CommissionHistoryModel>>>()
    val customerServiceCommission: LiveData<Resource<List<CommissionHistoryModel>>> = _customerServiceCommission


    fun getCustomerServiceCommission(requestModel: GrowthComissionRequestModel) {

        viewModelScope.launch {

            customerRepository
                    .getCustomerServiceCommission(requestModel)
                    .onStart {
                        _customerServiceCommission.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _customerServiceCommission.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _customerServiceCommission.postValue(Resource.Success(response))
                    }
        }

    }

    private val _customerShoppingCommission = MutableLiveData<Resource<List<CommissionHistoryModel>>>()
    val customerShoppingCommission: LiveData<Resource<List<CommissionHistoryModel>>> = _customerShoppingCommission


    fun getCustomerShoppingCommission(requestModel: GrowthComissionRequestModel) {

        viewModelScope.launch {

            customerRepository
                    .getCustomerShoppingCommission(requestModel)
                    .onStart {
                        _customerShoppingCommission.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _customerShoppingCommission.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _customerShoppingCommission.postValue(Resource.Success(response))
                    }
        }

    }

}