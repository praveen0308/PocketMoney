package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerGrowthResponse
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerGrowthActivityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository
): ViewModel(){

    private val _userModel: MutableLiveData<DataState<UserModel?>> = MutableLiveData()
    val userModel: LiveData<DataState<UserModel?>>
        get() = _userModel

    val userID = userPreferencesRepository.userId.asLiveData()
    val roleID = userPreferencesRepository.userRoleId.asLiveData()


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
}