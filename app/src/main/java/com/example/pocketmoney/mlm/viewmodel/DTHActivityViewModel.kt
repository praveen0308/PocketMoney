package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.DthCustomerDetail
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.model.serviceModels.*
import com.example.pocketmoney.mlm.repository.*
import com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui.Recharge
import com.example.pocketmoney.utils.Resource
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.WalletType
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DTHActivityViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val rechargeRepository: RechargeRepository,
    private val serviceRepository: ServiceRepository,
    private val walletRepository: WalletRepository,
    private val paytmRepository: PaytmRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val selectedPaymentMethod = MutableLiveData(PaymentEnum.WALLET)
    var rechargeAmount = MutableLiveData<Int>()
    val currentActivePage = MutableLiveData(0)
    val rechargeMobileNo  = MutableLiveData<String>()
    val rechargeMobileNumber  = MutableLiveData<String>()
    var requestId = ""
    val selectedOperator = MutableLiveData<ModelOperator>()
    lateinit var recharge : MobileRechargeModel

    val progressStatus = MutableLiveData<Int>()

    var transactionToken = ""
    lateinit var paytmResponseModel: PaytmResponseModel

    private val _dthOperators = MutableLiveData<List<ModelOperator>>()
    val dthOperators: LiveData<List<ModelOperator>> = _dthOperators

    fun getDTHOperators() {

        viewModelScope.launch {

            _dthOperators.postValue(rechargeRepository.getOperators(RechargeEnum.DTH))

        }

    }

    private val _dthCustomerDetail: MutableLiveData<Resource<DthCustomerDetail>> = MutableLiveData()
    val dthCustomerDetail: LiveData<Resource<DthCustomerDetail>> = _dthCustomerDetail

    fun getDthCustomerDetails(accountId:String,operatorCode:String){
        viewModelScope.launch {

            rechargeRepository.getDthCustomerDetails(accountId,operatorCode)
                .onStart {
                    _dthCustomerDetail.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _dthCustomerDetail.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _dthCustomerDetail.postValue(Resource.Success(response))
                }
        }
    }


    private val _rechargeHistory = MutableLiveData<Resource<List<RechargeHistoryModel>>>()
    val rechargeHistory: LiveData<Resource<List<RechargeHistoryModel>>> = _rechargeHistory
    fun getRechargeHistory(jsonObject: JsonObject) {
        viewModelScope.launch {
            serviceRepository
                .getUsedServiceHistory(jsonObject)
                .onStart {
                    _rechargeHistory.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _rechargeHistory.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _rechargeHistory.postValue(Resource.Success(response))
                }
        }

    }

    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    progressStatus.postValue(Recharge.CHECKING_WALLET_BALANCE)
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(Recharge.ERROR)
                        _walletBalance.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        progressStatus.postValue(Recharge.INSUFFICIENT_BALANCE)
                        _walletBalance.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                        Timber.d("Insufficient Wallet Balance !!!")

                    }else{
                        recharge = MobileRechargeModel()
                        recharge.UserID = userId
                        recharge.MobileNo = rechargeMobileNo.value!!
                        recharge.ServiceTypeID = 2
                        recharge.WalletTypeID = WalletType.Wallet.id
                        recharge.OperatorCode = selectedOperator.value!!.operatorCode!!
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 9

                        addUsedServiceDetail(recharge)
                        progressStatus.postValue(Recharge.ADDING_USED_SERVICE_DETAIL)
                    }
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }
    }
    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash

    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    progressStatus.postValue(Recharge.CHECKING_WALLET_BALANCE)
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        progressStatus.postValue(Recharge.INSUFFICIENT_BALANCE)
                        _pCash.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                    }else{
                        recharge = MobileRechargeModel()
                        recharge.UserID = userId
                        recharge.MobileNo = rechargeMobileNo.value!!
                        recharge.ServiceTypeID = 2
                        recharge.WalletTypeID = WalletType.PCash.id
                        recharge.OperatorCode = selectedOperator.value!!.operatorCode!!
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 9

                        addUsedServiceDetail(recharge)
                        progressStatus.postValue(Recharge.ADDING_USED_SERVICE_DETAIL)
                    }
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }


    private val _checkSum = MutableLiveData<Resource<String>>()
    var checkSum : LiveData<Resource<String>> = _checkSum

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    progressStatus.postValue(Recharge.LOADING)
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    progressStatus.postValue(Recharge.CHECKSUM_RECEIVED)
                    transactionToken = response
                    _checkSum.postValue(Resource.Success(response))
                }
        }

    }

    //    step 1
    private val _addUsedServiceDetailResponse = MutableLiveData<Resource<Int>>()
    val addUsedServiceDetailResponse: LiveData<Resource<Int>> = _addUsedServiceDetailResponse


    fun addUsedServiceDetail(usedServiceDetailModel: MobileRechargeModel) {
        viewModelScope.launch {
            serviceRepository
                .addUsedServiceDetail(usedServiceDetailModel)
                .onStart {
                    progressStatus.postValue(Recharge.LOADING)
                    _addUsedServiceDetailResponse.postValue(Resource.Loading(true))
//                    progressStatus.postValue(true)
                }
                .catch { exception ->
                    exception.message?.let {
                        _addUsedServiceDetailResponse.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while adding used service detail.")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { response->
                    if (response>0){
                        getUsedServiceRequestId(usedServiceDetailModel.UserID!!,rechargeMobileNo.value!!)
                    }
                    _addUsedServiceDetailResponse.postValue(Resource.Success(response))
                }
        }

    }

    // step 2
    private val _usedServiceRequestId = MutableLiveData<Resource<String>>()
    val usedServiceRequestId: LiveData<Resource<String>> = _usedServiceRequestId

    fun getUsedServiceRequestId(userId: String,mobileNo: String) {
        viewModelScope.launch {
            serviceRepository
                .getUsedServiceRequestId(userId, mobileNo)
                .onStart {
                    progressStatus.postValue(Recharge.LOADING)
                    _usedServiceRequestId.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _usedServiceRequestId.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while getting used service request id.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    recharge.RequestID = response
                    _usedServiceRequestId.postValue(Resource.Success(response))
                    if (serviceRepository.selectedPaymentMethod== PaymentEnum.PAYTM){
                        progressStatus.postValue(Recharge.START_PAYMENT_GATEWAY)
                        addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
                                ReferenceTransactionId = response,   // request id
                                ServiceTypeId = 2,
                                WalletTypeId = WalletType.OnlinePayment.id,
                                TxnAmount = paytmResponseModel.TXNAMOUNT,
                                Currency = paytmResponseModel.CURRENCY,
                                TransactionTypeId = 1,
                                IsCredit =  false,
                                TxnId = paytmResponseModel.TXNID,
                                Status = paytmResponseModel.STATUS,
                                RespCode = paytmResponseModel.RESPCODE,
                                RespMsg = paytmResponseModel.RESPMSG,
                                BankTxnId = paytmResponseModel.BANKTXNID,
                                BankName = paytmResponseModel.GATEWAYNAME,
                                PaymentMode = paytmResponseModel.PAYMENTMODE
                            )
                        )
                    }else{
                        callSampurnaDthRechargeService(recharge)
                    }
                }
        }

    }


    // step 3
    private val _mobileRechargeModel = MutableLiveData<Resource<MobileRechargeModel>>()
    val mobileRechargeModel: LiveData<Resource<MobileRechargeModel>> = _mobileRechargeModel

    fun callSampurnaDthRechargeService(mobileRechargeModel: MobileRechargeModel) {

        viewModelScope.launch {
            serviceRepository
                .callSamupurnaDthRechargeService(mobileRechargeModel)
                .onStart {
                    progressStatus.postValue(Recharge.LOADING)
                    _mobileRechargeModel.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _mobileRechargeModel.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while calling SampurnaRechargeService.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    when(response.Status){
                        "SUCCESS"->{
                            if (serviceRepository.selectedPaymentMethod== PaymentEnum.PAYTM){
                                if (paytmResponseModel.PAYMENTMODE != "UPI"){
                                    val amountDeduct = (paytmResponseModel.TXNAMOUNT?.toDouble() ?: 0.0) * 2 / 100
                                    walletChargeDeduct(mobileRechargeModel.UserID!!,2,amountDeduct,recharge.RequestID!!,18)
                                }else{
                                    progressStatus.postValue(Recharge.RECHARGE_SUCCESSFUL)
                                }
                            }else{
                                progressStatus.postValue(Recharge.RECHARGE_SUCCESSFUL)

                            }

                        }
                        "FAILED"->{
                            progressStatus.postValue(Recharge.RECHARGE_FAILED)
                        }
                        "PENDING"->{
                            progressStatus.postValue(Recharge.PENDING)
                        }
                    }

                    _mobileRechargeModel.postValue(Resource.Success(response))
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
                    progressStatus.postValue(Recharge.LOADING)
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPaymentTransResponse.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while calling SampurnaRechargeService.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    if (paytmResponseModel.STATUS == "SUCCESS"){
                        Timber.d("Payment Gateway response was successful.")
                        callSampurnaDthRechargeService(recharge)
                    }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                        Timber.d("Payment Gateway response was failed.")
                        progressStatus.postValue(Recharge.RECHARGE_FAILED)
                        _addPaymentTransResponse.postValue(Resource.Error("Payment failed !!!"))

                    }
                    _addPaymentTransResponse.postValue(Resource.Success(response))
                }
        }
    }


    private val _walletChargeDeducted = MutableLiveData<Resource<Int>>()
    val walletChargeDeducted : LiveData<Resource<Int>> = _walletChargeDeducted

    fun walletChargeDeduct(userId: String,walletId:Int,amount:Double,requestId:String,serviceId:Int) {

        viewModelScope.launch {
            walletRepository
                .walletChargeDeduction(userId, walletId, amount, requestId, serviceId)
                .onStart {
                    progressStatus.postValue(Recharge.LOADING)
                    _walletChargeDeducted.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletChargeDeducted.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(Recharge.ERROR)
                        Timber.d("Error occurred while wallet charge deduction.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    progressStatus.postValue(Recharge.RECHARGE_SUCCESSFUL)
                    _walletChargeDeducted.postValue(Resource.Success(response))
                }
        }
    }

}