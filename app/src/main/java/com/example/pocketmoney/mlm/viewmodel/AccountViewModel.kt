package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
        private val accountRepository: AccountRepository
):ViewModel(){
    private val _userModel: MutableLiveData<DataState<UserModel?>> = MutableLiveData()
    val userModel: LiveData<DataState<UserModel?>>
        get() = _userModel

    val welcomeStatus = accountRepository.welcomeState.asLiveData()

    val userID = accountRepository.userID.asLiveData()
    val roleID = accountRepository.roleID.asLiveData()

    fun updateWelcomeStatus(status:Int)=viewModelScope.launch {
        accountRepository.updateWelcomeStatus(status)
    }

    fun doLogin(userName:String,password:String){
        viewModelScope.launch {

            accountRepository.doLogin(userName, password)
                    .onEach { dataState ->
                        _userModel.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    private val _isAccountDuplicate = MutableLiveData<Resource<Boolean>>()
    val isAccountDuplicate: LiveData<Resource<Boolean>> = _isAccountDuplicate


    fun checkAccountAlreadyExist(userId: String) {

        viewModelScope.launch {

            accountRepository
                    .checkAccountAlreadyExist(userId)
                    .onStart {
                        _isAccountDuplicate.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _isAccountDuplicate.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _isAccountDuplicate.postValue(Resource.Success(response))
                    }
        }

    }

    private val _isSuccessfullyRegistered = MutableLiveData<Resource<Boolean>>()
    val isSuccessfullyRegistered: LiveData<Resource<Boolean>> = _isSuccessfullyRegistered

    fun registerUser(customerDetail: ModelCustomerDetail) {
        viewModelScope.launch {
            accountRepository
                    .registerUser(customerDetail)
                    .onStart {
                        _isSuccessfullyRegistered.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _isSuccessfullyRegistered.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _isSuccessfullyRegistered.postValue(Resource.Success(response))
                    }
        }

    }

    private val _sponsorName = MutableLiveData<Resource<String>>()
    val sponsorName: LiveData<Resource<String>> = _sponsorName

    fun getSponsorName(id:String) {
        viewModelScope.launch {
            accountRepository
                    .getSponsorName(id)
                    .onStart {
                        _sponsorName.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _sponsorName.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _sponsorName.postValue(Resource.Success(response))
                    }
        }

    }

    private val _userName = MutableLiveData<Resource<String>>()
    val userName: LiveData<Resource<String>> = _userName

    fun getUserName(id:String) {
        viewModelScope.launch {
            accountRepository
                    .getUserName(id)
                    .onStart {
                        _userName.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _userName.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _userName.postValue(Resource.Success(response))
                    }
        }

    }

    private val _isAccountActive = MutableLiveData<Resource<Boolean>>()
    val isAccountActive: LiveData<Resource<Boolean>> = _isAccountActive

    fun checkIsAccountActive(id:String) {
        viewModelScope.launch {
            accountRepository
                    .isUserAccountActive(id)
                    .onStart {
                        _isAccountActive.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _isAccountActive.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _isAccountActive.postValue(Resource.Success(response))
                    }
        }

    }


    private val _userMenus = MutableLiveData<Resource<List<UserMenu>>>()
    val userMenus: LiveData<Resource<List<UserMenu>>> = _userMenus

    fun getUserMenus(userId: String) {
        viewModelScope.launch {
            accountRepository
                    .getUserMenus(userId)
                    .onStart {
                        _userMenus.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _userMenus.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _userMenus.postValue(Resource.Success(response))
                    }
        }
    }

}