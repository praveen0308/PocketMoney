package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.sampurna.pocketmoney.mlm.model.CustomerDetailResponse
import com.sampurna.pocketmoney.mlm.model.ModelContact
import com.sampurna.pocketmoney.mlm.repository.CustomerRepository
import com.sampurna.pocketmoney.mlm.repository.RechargeRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.utils.DataState
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class B2BTransferViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val rechargeRepository: RechargeRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val recipientUserId = MutableLiveData<String>()

    fun setRecipientUserId(userId: String) {
        recipientUserId.postValue(userId)
    }

    private val _customerDetail = MutableLiveData<Resource<CustomerDetailResponse?>>()
    val customerDetail: LiveData<Resource<CustomerDetailResponse?>> = _customerDetail


    fun getCustomerDetail(userId: String) {

        viewModelScope.launch {

            customerRepository
                .getCustomerDetail(userId)
                .onStart {
                    _customerDetail.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _customerDetail.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _customerDetail.postValue(Resource.Success(response))
                }
        }

    }

    private val _contactList: MutableLiveData<DataState<List<ModelContact>>> = MutableLiveData()
    val contactList: LiveData<DataState<List<ModelContact>>>
        get() = _contactList


    fun getContactList(){
        viewModelScope.launch {

            rechargeRepository.getContactList()
                .onEach { dataState ->
                    _contactList.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    private val _b2bTransferResponse = MutableLiveData<Resource<Int>>()
    val b2bTransferResponse: LiveData<Resource<Int>> = _b2bTransferResponse


    fun b2bTransfer(requestData:JsonObject) {

        viewModelScope.launch {

            walletRepository
                .transferB2BWallet(requestData)
                .onStart {
                    _b2bTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _b2bTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _b2bTransferResponse.postValue(Resource.Success(response))
                }
        }

    }

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {

                        _walletBalance.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { _balance->

                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }
    }


}