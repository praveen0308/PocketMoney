package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.mlm.model.mlmModels.*
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {



    private val _customerGrowth = MutableLiveData<Resource<CustomerGrowthResponse>>()
    val customerGrowth: LiveData<Resource<CustomerGrowthResponse>> = _customerGrowth


    fun getCustomerGrowth(requestModel1: CustomerRequestModel1) {

        viewModelScope.launch {

            customerRepository
                    .getCustomerGrowth(requestModel1)
                    .onStart {
                        _customerGrowth.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _customerGrowth.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _customerGrowth.postValue(Resource.Success(response))
                    }
        }

    }

    private val _couponList = MutableLiveData<Resource<List<CouponModel>>>()
    val couponList: LiveData<Resource<List<CouponModel>>> = _couponList


    fun getCouponList(userID:String,roleId:Int,fromDate:String,toDate:String) {

        viewModelScope.launch {

            customerRepository
                .getCouponList(userID, roleId, fromDate, toDate)
                .onStart {
                    _couponList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _couponList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _couponList.postValue(Resource.Success(response))
                }
        }

    }

    private val _customerProfileInfo = MutableLiveData<Resource<CustomerProfileModel>>()
    val customerProfileInfo: LiveData<Resource<CustomerProfileModel>> = _customerProfileInfo


    fun getUserProfileInfo(id: String) {

        viewModelScope.launch {

            customerRepository
                .getUserProfile(id)
                .onStart {
                    _customerProfileInfo.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _customerProfileInfo.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _customerProfileInfo.postValue(Resource.Success(response))
                }
        }

    }


    private val _complaintList = MutableLiveData<Resource<List<CustomerComplaintModel>>>()
    val complaintList: LiveData<Resource<List<CustomerComplaintModel>>> = _complaintList


    fun getComplaintList(userID:String,roleId:Int,fromDate:String,toDate:String,filter:String,condition:String) {

        viewModelScope.launch {

            customerRepository
                .getComplaintHistory(userID, roleId, fromDate, toDate,filter, condition)
                .onStart {
                    _complaintList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _complaintList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _complaintList.postValue(Resource.Success(response))
                }
        }

    }


    fun getComplaintList(userID:String,roleId:Int,fromDate:String,toDate:String) {

        viewModelScope.launch {

            customerRepository
                    .getComplaintHistory(userID, roleId, fromDate, toDate,"", "")
                    .onStart {
                        _complaintList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _complaintList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _complaintList.postValue(Resource.Success(response))
                    }
        }

    }
}