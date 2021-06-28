package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintListViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customerRepository: CustomerRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    private val _complaintList = MutableLiveData<Resource<List<CustomerComplaintModel>>>()
    val complaintList: LiveData<Resource<List<CustomerComplaintModel>>> = _complaintList


    fun getComplaintList(userID:String,roleId:Int,fromDate:String,toDate:String,filter:String,condition:String) {

        viewModelScope.launch {

            customerRepository
                .getComplaintHistory(userID, roleId, fromDate, toDate,filter, condition)
                .onStart {
                    _complaintList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _complaintList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _complaintList.postValue(Resource.Success(response))
                }
        }

    }


    fun getComplaintList(userID:String,roleId:Int,fromDate:String,toDate:String) {

        viewModelScope.launch {

            customerRepository
                .getComplaintHistory(userID, roleId, fromDate, toDate,"", "")
                .onStart {
                    _complaintList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _complaintList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _complaintList.postValue(Resource.Success(response))
                }
        }

    }

}