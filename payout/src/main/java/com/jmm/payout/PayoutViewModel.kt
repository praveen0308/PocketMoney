package com.jmm.payout

import androidx.lifecycle.*
import com.jmm.model.SMSResponseModel
import com.jmm.model.payoutmodels.*
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.repository.*
import com.jmm.util.Resource
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

    val payoutTransferMoneyPageState =
        MutableLiveData<PayoutTransferState>(PayoutTransferState.Idle)


    fun getWalletBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    payoutTransferMoneyPageState.postValue(PayoutTransferState.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        payoutTransferMoneyPageState.postValue(
                            PayoutTransferState.Error(
                                "Something went wrong!!!",
                                it
                            )
                        )

                        Timber.d("Error caused by >>>> getWalletBalance")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance ->
                    payoutTransferMoneyPageState.postValue(PayoutTransferState.Idle)
                    payoutTransferMoneyPageState.postValue(
                        PayoutTransferState.GotWalletBalance(_balance)
                    )
                }
        }

    }


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


    fun initiatePayoutTransfer(beneficiaryId: String, paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            payoutRepository
                .initiatePayoutTransfer(beneficiaryId, paytmRequestData)
                .onStart {
                    payoutTransferMoneyPageState.postValue(PayoutTransferState.Loading(true))

                }
                .catch { exception ->
                    exception.message?.let {
                        payoutTransferMoneyPageState.postValue(
                            PayoutTransferState.Error(
                                "Something went wrong !!!",
                                it
                            )
                        )
                        Timber.d("Error caused by >>>> initiatePayoutTransfer")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response ->
                    if (response.status == "SUCCESS") {
                        payoutTransferMoneyPageState.postValue(
                            PayoutTransferState.PayoutTransactionSuccessful(
                                response
                            )
                        )
                    } else {
                        payoutTransferMoneyPageState.postValue(
                            PayoutTransferState.PayoutTransactionFailed(
                                response
                            )
                        )
                    }

                }
        }
    }

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
                    payoutTransferMoneyPageState.postValue(PayoutTransferState.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        payoutTransferMoneyPageState.postValue(
                            PayoutTransferState.SMSGenerationFailed(
                                it
                            )
                        )
                        Timber.d("Error caused by >>> sendSmsOfTransaction")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    payoutTransferMoneyPageState.postValue(PayoutTransferState.SentSMS(it))
                }
        }
    }


}


sealed class PayoutTransferState {
    object Idle : PayoutTransferState()
    data class Loading(val isLoading: Boolean) : PayoutTransferState()
    data class Error(val message: String, val exception: String) : PayoutTransferState()
    data class FetchedPayoutCustomer(val customer: PayoutCustomer) : PayoutTransferState()
    data class AddedPayoutCustomer(val response: Int) : PayoutTransferState()
    data class PayoutCustomerDetail(val payoutCustomer: PayoutCustomer) : PayoutTransferState()
    data class GotWalletBalance(val balance: Double) : PayoutTransferState()
    data class BankList(val banks: List<BankModel>) : PayoutTransferState()
    data class BeneficiaryAdded(val response: Int) : PayoutTransferState()
    data class SentSMS(val response: SMSResponseModel) : PayoutTransferState()
    data class SMSGenerationFailed(val response: String) : PayoutTransferState()
    data class PayoutTransactionSuccessful(val response: PayoutTransactionResponse) :
        PayoutTransferState()

    data class PayoutTransactionFailed(val response: PayoutTransactionResponse) :
        PayoutTransferState()

}