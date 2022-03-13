package com.jmm.manage_address

import androidx.lifecycle.*
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.model.shopping_models.ModelCity
import com.jmm.model.shopping_models.ModelState
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.shopping_repo.AddressRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

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
                .collect {
                    _customerAddressList.postValue(Resource.Success(it))
                }
        }
    }

    private val _isSuccessfullyAdded: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isSuccessFullyAdded: LiveData<Resource<Boolean>> = _isSuccessfullyAdded

    fun addNewAddress(modelAddress: ModelAddress){
        viewModelScope.launch {
            addressRepository
                .addNewAddress(modelAddress)
                .onStart {
                    _isSuccessfullyAdded.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isSuccessfullyAdded.postValue(Resource.Error(it))
                    }
                }
                .collect {
                    _isSuccessfullyAdded.postValue(Resource.Success(it))
                }
        }
    }

    private val _isSuccessfullyUpdated: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isSuccessfullyUpdated: LiveData<Resource<Boolean>> = _isSuccessfullyUpdated

    fun updateAddress(modelAddress: ModelAddress){
        viewModelScope.launch {
            addressRepository
                .updateAddress(modelAddress)
                .onStart {
                    _isSuccessfullyUpdated.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isSuccessfullyUpdated.postValue(Resource.Error(it))
                    }
                }
                .collect {
                    _isSuccessfullyUpdated.postValue(Resource.Success(it))
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