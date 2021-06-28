package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.repository.WalletRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
        private val accountRepository: AccountRepository,
        private val userPreferencesRepository: UserPreferencesRepository,
        private val walletRepository: WalletRepository

):ViewModel(){
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    fun clearUserInfo(){
        viewModelScope.launch {
            userPreferencesRepository.clearUserInfo()
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

//    private val _userName = MutableLiveData<Resource<String>>()
//    val userName: LiveData<Resource<String>> = _userName
//
//    fun getUserName(id:String) {
//        viewModelScope.launch {
//            accountRepository
//                    .getUserName(id)
//                    .onStart {
//                        _userName.postValue(Resource.Loading(true))
//                    }
//                    .catch { exception ->
//                        exception.message?.let {
//                            _userName.postValue(Resource.Error(it))
//                        }
//                    }
//                    .collect { response->
//                        _userName.postValue(Resource.Success(response))
//                    }
//        }
//
//    }

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

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance


    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash


    fun getWalletBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletBalance.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }


    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }


}