package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
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
    private val addressRepository: AddressRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()

    private val _customerAddressList: MutableLiveData<Resource<List<ModelAddress>>> = MutableLiveData()
    val customerAddressList: LiveData<Resource<List<ModelAddress>>> = _customerAddressList

    fun getCustomerAddressList(userId: String){
        viewModelScope.launch {

            addressRepository.getCustomerAddressByUserId(userId)
                .onStart {
                    _customerAddressList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _customerAddressList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _customerAddressList.postValue(Resource.Success(response))
                }
        }
    }

    private val _addressDetail = MutableLiveData<Resource<ModelAddress>>()
    val addressDetail : LiveData<Resource<ModelAddress>> = _addressDetail

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