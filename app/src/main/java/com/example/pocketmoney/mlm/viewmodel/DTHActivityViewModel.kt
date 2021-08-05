package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.*
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.RechargeRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DTHActivityViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val rechargeRepository: RechargeRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val selectedOperator = MutableLiveData<ModelOperator>()

    private val _dthOperators = MutableLiveData<List<ModelOperator>>()
    val dthOperators: LiveData<List<ModelOperator>> = _dthOperators

    fun getDTHOperators() {

        viewModelScope.launch {

            _dthOperators.postValue(rechargeRepository.getOperators(RechargeEnum.DTH))

        }

    }


}