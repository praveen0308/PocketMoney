package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.mlm.model.mlmModels.CustomerProfileModel
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.CustomerRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

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


}