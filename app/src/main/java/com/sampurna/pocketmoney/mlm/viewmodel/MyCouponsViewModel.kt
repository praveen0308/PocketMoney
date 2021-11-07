package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.mlm.model.mlmModels.CouponModel
import com.sampurna.pocketmoney.mlm.repository.CustomerRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCouponsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

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


}