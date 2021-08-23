package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.model.serviceModels.PMWalletModel
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.PaytmRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.repository.WalletRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMoneyToWalletViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val paytmRepository: PaytmRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val mAmount = MutableLiveData(0.0)

    val RequestID : String = ""

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
                .collect { response ->
                    _isAccountDuplicate.postValue(Resource.Success(response))
                }
        }

    }
    private val _checkSum = MutableLiveData<Resource<String>>()
    val checkSum : LiveData<Resource<String>> = _checkSum

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _checkSum.postValue(Resource.Success(response))
                }
        }

    }

    private val _addCustWalletDetailResponse = MutableLiveData<Resource<String>>()
    val addCustWalletDetailResponse : LiveData<Resource<String>> = _addCustWalletDetailResponse

    fun addCustomerWalletDetails(pmWalletModel: PMWalletModel) {

        viewModelScope.launch {
            walletRepository
                .addCustomerWalletDetails(pmWalletModel)
                .onStart {
                    _addCustWalletDetailResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addCustWalletDetailResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addCustWalletDetailResponse.postValue(Resource.Success(response))
                }
        }

    }

    private val _addPaymentTransResponse = MutableLiveData<Resource<String>>()
    val addPaymentTransResponse : LiveData<Resource<String>> = _addPaymentTransResponse

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {

        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPaymentTransResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addPaymentTransResponse.postValue(Resource.Success(response))
                }
        }
    }

    private val _actionOnWalletDetailResponse = MutableLiveData<Resource<Int>>()
    val actionOnWalletDetailResponse : LiveData<Resource<Int>> = _actionOnWalletDetailResponse

    fun actionOnWalletDetail(requestId:String,comment:String,status:String,paymentMode:String) {

        viewModelScope.launch {
            walletRepository
                .actionOnWalletRequest(requestId, comment, status, paymentMode)
                .onStart {
                    _actionOnWalletDetailResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _actionOnWalletDetailResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _actionOnWalletDetailResponse.postValue(Resource.Success(response))
                }
        }

    }

    private val _addCompanyTransactionResponse = MutableLiveData<Resource<Int>>()
    val addCompanyTransactionResponse : LiveData<Resource<Int>> = _addCompanyTransactionResponse

    fun addCompanyTransactionResponse(transferBy:String,userId: String,
                                      amount:Double,walletType:Int,
                                      transType:Int,referenceId:String,action:String) {

        viewModelScope.launch {
            walletRepository
                .addCompanyTransactionDetail(transferBy, userId, amount, walletType, transType, referenceId, action)
                .onStart {
                    _addCompanyTransactionResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addCompanyTransactionResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addCompanyTransactionResponse.postValue(Resource.Success(response))
                }
        }

    }


}