package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.common.MailMessagingRepository
import com.sampurna.pocketmoney.common.SMSResponseModel
import com.sampurna.pocketmoney.mlm.model.payoutmodels.*
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.PayoutRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PayoutViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val payoutRepository: PayoutRepository,
    private val walletRepository: WalletRepository,
    private val mailMessagingRepository: MailMessagingRepository

) : ViewModel() {

    val btnState = MutableLiveData(0)
    var customerNumber = MutableLiveData("")
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val payoutType = MutableLiveData(1)
    val selectedBeneficiary = MutableLiveData<Beneficiary>()
    val progressStatus = MutableLiveData(0)

    val selectedBank = MutableLiveData("")
    val selectedBankIfsc = MutableLiveData("")


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
                .collect { _balance ->
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance: LiveData<Resource<Double>> = _walletBalance

    private val _addPayoutCustomer = MutableLiveData<Resource<Int>>()
    val addPayoutCustomer: LiveData<Resource<Int>> = _addPayoutCustomer

    fun addPayoutCustomer(customer: PayoutCustomer) {

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


    private val _payoutCustomer = MutableLiveData<Resource<PayoutCustomer?>>()
    val payoutCustomer: LiveData<Resource<PayoutCustomer?>> = _payoutCustomer

    fun searchPayoutCustomer(customerId: String) {

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
                .collect { response ->
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
                .collect { response ->
                    _isBeneficiaryAdded.postValue(Resource.Success(response))

                }
        }

    }

    private val _beneficiaryDetails = MutableLiveData<Resource<List<Beneficiary>>>()
    val beneficiaryDetails: LiveData<Resource<List<Beneficiary>>> = _beneficiaryDetails

    fun getBeneficiaries(customerId: String, transType: Int) {

        viewModelScope.launch {

            payoutRepository
                .getBeneficiaryDetails(customerId, transType)
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

    fun getPayoutTransactions(customerId: String, transType: Int) {

        viewModelScope.launch {

            payoutRepository
                .fetchPayoutCustomerTransactions(customerId, transType)
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

    private val _payoutTransferResponse = MutableLiveData<Resource<PayoutTransactionResponse>>()
    val payoutTransferResponse: LiveData<Resource<PayoutTransactionResponse>> =
        _payoutTransferResponse

    fun initiateBankTransfer(beneficiaryID: String, paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateBankTransfer(beneficiaryID, paytmRequestData)
                .onStart {
                    _payoutTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _payoutTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _payoutTransferResponse.postValue(Resource.Success(response))
                }
        }
    }

    fun initiateUpiTransfer(beneficiaryID: String, paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateUpiTransfer(beneficiaryID, paytmRequestData)
                .onStart {
                    _payoutTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _payoutTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _payoutTransferResponse.postValue(Resource.Success(response))
                }
        }
    }

    fun initiatePaytmTransfer(beneficiaryID: String, paytmRequestData: PaytmRequestData) {

        viewModelScope.launch {

            payoutRepository
                .initiateWalletTransfer(beneficiaryID, paytmRequestData)
                .onStart {
                    _payoutTransferResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _payoutTransferResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response ->
                    _payoutTransferResponse.postValue(Resource.Success(response))
                }
        }
    }


    private val _sendSmsResponse = MutableLiveData<Resource<SMSResponseModel>>()
    val sendSmsResponse: LiveData<Resource<SMSResponseModel>> = _sendSmsResponse

    fun sendSmsOfTransaction(
        mobileNo: String,
        senderName: String,
        amount: String,
        accountNumber: String,
        beneficiary: String,
        mode: String
    ) {
        viewModelScope.launch {
            mailMessagingRepository
                .sendPayoutSMS(mobileNo, senderName, amount, accountNumber, beneficiary, mode)
                .onStart {
                    _sendSmsResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _sendSmsResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error caused by >>>> sendSmsOfTransaction")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    _sendSmsResponse.postValue(Resource.Success(it))
                }
        }
    }


}