package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.model.payoutmodels.BankModel
import com.example.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutCustomer
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutTransaction
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.PayoutRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.repository.WalletRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayoutViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val payoutRepository: PayoutRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {

    val btnState = MutableLiveData(0)
    var customerNumber = MutableLiveData("")
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val payoutType = MutableLiveData(1)
    val selectedBeneficiary = MutableLiveData<Beneficiary>()

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


    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance


    private val _addPayoutCustomer = MutableLiveData<Resource<Int>>()
    val addPayoutCustomer: LiveData<Resource<Int>> = _addPayoutCustomer

    fun addPayoutCustomer(customer:PayoutCustomer) {

        viewModelScope.launch {

            payoutRepository
                .addPayoutCustomer(customer)
                .onStart {
                    _addPayoutCustomer.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPayoutCustomer.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _addPayoutCustomer.postValue(Resource.Success(response))
                }
        }

    }


    private val _payoutCustomer = MutableLiveData<Resource<PayoutCustomer>>()
    val payoutCustomer: LiveData<Resource<PayoutCustomer>> = _payoutCustomer

    fun searchPayoutCustomer(customerId:String) {

        viewModelScope.launch {

            payoutRepository
                .searchPayoutCustomer(customerId)
                .onStart {
                    _payoutCustomer.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _payoutCustomer.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _payoutCustomer.postValue(Resource.Success(response))
                }
        }

    }

    private val _banks = MutableLiveData<Resource<List<BankModel>>>()
    val banks: LiveData<Resource<List<BankModel>>> = _banks

    fun getBanks() {
        viewModelScope.launch {
            payoutRepository
                .getBankIFSC()
                .onStart {
                    _banks.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _banks.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _banks.postValue(Resource.Success(response))

                }
        }

    }

    private val _isBeneficiaryAdded = MutableLiveData<Resource<Int>>()
    val isBeneficiaryAdded: LiveData<Resource<Int>> = _isBeneficiaryAdded

    fun addBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            payoutRepository
                .addNewBeneficiary(beneficiary)
                .onStart {
                    _isBeneficiaryAdded.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isBeneficiaryAdded.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isBeneficiaryAdded.postValue(Resource.Success(response))

                }
        }

    }

    private val _beneficiaryDetails = MutableLiveData<Resource<List<Beneficiary>>>()
    val beneficiaryDetails: LiveData<Resource<List<Beneficiary>>> = _beneficiaryDetails

    fun getBeneficiaries(customerId:String,transType:Int) {

        viewModelScope.launch {

            payoutRepository
                .getBeneficiaryDetails(customerId,transType)
                .onStart {
                    _beneficiaryDetails.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _beneficiaryDetails.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _beneficiaryDetails.postValue(Resource.Success(response))
                }
        }
    }

    private val _payoutTransactions = MutableLiveData<Resource<List<PayoutTransaction>>>()
    val payoutTransactions: LiveData<Resource<List<PayoutTransaction>>> = _payoutTransactions

    fun getPayoutTransactions(customerId:String,transType:Int) {

        viewModelScope.launch {

            payoutRepository
                .fetchPayoutCustomerTransactions(customerId,transType)
                .onStart {
                    _payoutTransactions.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _payoutTransactions.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _payoutTransactions.postValue(Resource.Success(response))
                }
        }
    }


    private val _bankTransferResponse = MutableLiveData<Resource<Int>>()
    val bankTransferResponse: LiveData<Resource<Int>> = _bankTransferResponse

    fun initiateBankTransfer(customerId:String,paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateBankTransfer(customerId,paytmRequestData)
                .onStart {
                    _bankTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _bankTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _bankTransferResponse.postValue(Resource.Success(response))
                }
        }
    }

    private val _upiTransferResponse = MutableLiveData<Resource<Int>>()
    val upiTransferResponse: LiveData<Resource<Int>> = _upiTransferResponse

    fun initiateUpiTransfer(customerId:String,paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateUpiTransfer(customerId,paytmRequestData)
                .onStart {
                    _upiTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _upiTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _upiTransferResponse.postValue(Resource.Success(response))
                }
        }
    }

    private val _paytmTransferResponse = MutableLiveData<Resource<Int>>()
    val paytmTransferResponse: LiveData<Resource<Int>> = _paytmTransferResponse

    fun initiatePaytmTransfer(customerId:String,paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateWalletTransfer(customerId,paytmRequestData)
                .onStart {
                    _paytmTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _paytmTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _paytmTransferResponse.postValue(Resource.Success(response))
                }
        }
    }
}