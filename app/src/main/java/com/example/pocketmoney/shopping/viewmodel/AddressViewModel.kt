package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.model.ModelCity
import com.example.pocketmoney.shopping.model.ModelState
import com.example.pocketmoney.shopping.repository.AddressRepository
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
): ViewModel() {

    private val _customerAddressList: MutableLiveData<DataState<List<ModelAddress>>> = MutableLiveData()
    val customerAddressList: LiveData<DataState<List<ModelAddress>>>
        get() = _customerAddressList

    private val _isSuccessfullyAdded: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val isSuccessFullyAdded: LiveData<DataState<Boolean>>
        get() = _isSuccessfullyAdded

    private val _isSuccessfullyUpdated: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val isSuccessfullyUpdated: LiveData<DataState<Boolean>>
        get() = _isSuccessfullyUpdated


    private val _addressDetail = MutableLiveData<Resource<ModelAddress>>()
    val addressDetail : LiveData<Resource<ModelAddress>> = _addressDetail

    fun getCustomerAddressList(userId: String){
        viewModelScope.launch {

            addressRepository.getCustomerAddressByUserId(userId)
                .onEach { dataState ->
                    _customerAddressList.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    fun addNewAddress(modelAddress: ModelAddress){
        viewModelScope.launch {

            addressRepository.addNewAddress(modelAddress)
                .onEach { dataState ->
                    _isSuccessfullyAdded.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    fun updateAddress(modelAddress: ModelAddress){
        viewModelScope.launch {

            addressRepository.updateAddress(modelAddress)
                .onEach { dataState ->
                    _isSuccessfullyUpdated.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    fun getAddressDetails(addressId:Int,userId: String) {

        viewModelScope.launch {

            addressRepository
                    .getAddressDetailById(addressId,userId)
                    .onStart {
                        _addressDetail.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _addressDetail.postValue(Resource.Error(it))
                        }
                    }
                    .collect { detail->
                        _addressDetail.postValue(Resource.Success(detail))
                    }
        }

    }

    private val _shippingCharge = MutableLiveData<Resource<Double>>()
    val shippingCharge : LiveData<Resource<Double>> = _shippingCharge

    fun getShippingCharge(addressId:Int,userId: String) {

        viewModelScope.launch {

            addressRepository
                    .getShippingCharge(addressId,userId)
                    .onStart {
                        _shippingCharge.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _shippingCharge.postValue(Resource.Error(it))
                        }
                    }
                    .collect { charge->
                        _shippingCharge.postValue(Resource.Success(charge))
                    }
        }

    }

    private val _statesList = MutableLiveData<Resource<List<ModelState>>>()
    val stateList : LiveData<Resource<List<ModelState>>> = _statesList

    fun getAllStates() {

        viewModelScope.launch {

            addressRepository
                    .getAllStates()
                    .onStart {
                        _statesList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _statesList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { list->
                        _statesList.postValue(Resource.Success(list))
                    }
        }

    }


    private val _citiesList = MutableLiveData<Resource<List<ModelCity>>>()
    val citiesList : LiveData<Resource<List<ModelCity>>> = _citiesList

    fun getCitiesByStateCode(stateCode:String) {

        viewModelScope.launch {

            addressRepository
                    .getCitiesByStateCode(stateCode)
                    .onStart {
                        _citiesList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _citiesList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { list->
                        _citiesList.postValue(Resource.Success(list))
                    }
        }

    }



}