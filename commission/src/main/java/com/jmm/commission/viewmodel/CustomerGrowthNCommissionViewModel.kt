package com.jmm.commission.viewmodel

import androidx.lifecycle.*
import com.jmm.model.UserModel
import com.jmm.model.mlmModels.*
import com.jmm.model.myEnums.FilterEnum
import com.jmm.repository.AccountRepository
import com.jmm.repository.CustomerRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.util.DataState
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerGrowthNCommissionViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val customerRepository: CustomerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
):ViewModel(){

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
    val selectedTimeFilter = MutableLiveData(FilterEnum.LAST_MONTH)
    fun assignSelectedTimeFilter(enum: FilterEnum){
        selectedTimeFilter.postValue(enum)
    }
}