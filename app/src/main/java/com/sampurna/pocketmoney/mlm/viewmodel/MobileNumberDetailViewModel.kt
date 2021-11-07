package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.mlm.model.*
import com.sampurna.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.RechargeRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MobileNumberDetailViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val rechargeRepository: RechargeRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val selectedOperator = MutableLiveData<String>()
    val selectedCircle = MutableLiveData<String>()

    private val _mobileOperators = MutableLiveData<List<ModelOperator>>()
    val mobileOperators: LiveData<List<ModelOperator>> = _mobileOperators

    fun getMobileOperators() {

        viewModelScope.launch {
            _mobileOperators.postValue(rechargeRepository.getOperators(RechargeEnum.PREPAID))

        }

    }
    private val _mobileServiceCircleList = MutableLiveData<Resource<List<IdNameModel>>>()
    val mobileServiceCircleList: LiveData<Resource<List<IdNameModel>>> = _mobileServiceCircleList


    fun getMobileServiceCircleList(providerID:Int=1) {

        viewModelScope.launch {

            rechargeRepository
                .getMobileServiceCircle(providerID)
                .onStart {
                    _mobileServiceCircleList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _mobileServiceCircleList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _mobileServiceCircleList.postValue(Resource.Success(response))
                }
        }

    }


}