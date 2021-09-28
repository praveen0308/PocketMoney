package com.example.pocketmoney.common

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ComplainModel
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatActivityViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    private val _isCompliantAdded = MutableLiveData<Resource<String>>()
    val isCompliantAdded: LiveData<Resource<String>> = _isCompliantAdded

    fun addServiceComplaint(requestId: String,transactionId:String,userId:String,comment:String) {
        viewModelScope.launch {
            customerRepository
                .addServiceComplain(requestId, transactionId, userId, comment)
                .onStart {
                    _isCompliantAdded.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isCompliantAdded.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isCompliantAdded.postValue(Resource.Success(response))
                }
        }
    }


    private val _complaintChat = MutableLiveData<Resource<List<ComplainModel>>>()
    val complaintChat: LiveData<Resource<List<ComplainModel>>> = _complaintChat

    fun getComplaintChat(transactionId:String) {
        viewModelScope.launch {
            customerRepository
                .getComplaintChat(transactionId)
                .onStart {
                    _complaintChat.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _complaintChat.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _complaintChat.postValue(Resource.Success(response))
                }
        }
    }

}