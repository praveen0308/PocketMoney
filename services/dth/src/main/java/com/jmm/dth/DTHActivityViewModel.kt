package com.jmm.dth

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.jmm.dth.DthRecharge.Companion.ADDING_USED_SERVICE_DETAIL
import com.jmm.dth.DthRecharge.Companion.CHECKING_WALLET_BALANCE
import com.jmm.dth.DthRecharge.Companion.CHECKSUM_RECEIVED
import com.jmm.dth.DthRecharge.Companion.ERROR
import com.jmm.dth.DthRecharge.Companion.INSUFFICIENT_BALANCE
import com.jmm.dth.DthRecharge.Companion.LOADING
import com.jmm.dth.DthRecharge.Companion.PENDING
import com.jmm.dth.DthRecharge.Companion.RECHARGE_FAILED
import com.jmm.dth.DthRecharge.Companion.RECHARGE_SUCCESSFUL
import com.jmm.dth.DthRecharge.Companion.START_PAYMENT_GATEWAY
import com.jmm.model.DthCustomerDetail
import com.jmm.model.ModelOperator
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.*
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

            _dthOperators.postValue(getDTHOperatorList())

        }

    }
    private fun getDTHOperatorList(): List<ModelOperator> {
        val operators= mutableListOf<ModelOperator>()

        operators.add(ModelOperator("Tata Sky",R.drawable.ic_tata_sky,"19"))
        operators.add(ModelOperator("Airtel DTH",R.drawable.ic_airtel,"22"))
        operators.add(ModelOperator("Big TV",R.drawable.ic_big_tv,"18"))
        operators.add(ModelOperator("Dish TV",R.drawable.ic_dish_tv,"17"))
        operators.add(ModelOperator("Sun Direct",R.drawable.ic_sun_direct,"20"))
        operators.add(ModelOperator("Videocon D2h",R.drawable.ic_videocon_d2h,"21"))
        return operators
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
                    progressStatus.postValue(CHECKING_WALLET_BALANCE)
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _walletBalance.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
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
                        progressStatus.postValue(ADDING_USED_SERVICE_DETAIL)
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
                    progressStatus.postValue(CHECKING_WALLET_BALANCE)
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
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
                        progressStatus.postValue(ADDING_USED_SERVICE_DETAIL)
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
                    progressStatus.postValue(LOADING)
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    progressStatus.postValue(CHECKSUM_RECEIVED)
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
                    progressStatus.postValue(LOADING)
                    _addUsedServiceDetailResponse.postValue(Resource.Loading(true))
//                    progressStatus.postValue(true)
                }
                .catch { exception ->
                    exception.message?.let {
                        _addUsedServiceDetailResponse.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
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
                    progressStatus.postValue(LOADING)
                    _usedServiceRequestId.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _usedServiceRequestId.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while getting used service request id.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    recharge.RequestID = response
                    _usedServiceRequestId.postValue(Resource.Success(response))
                    if (serviceRepository.selectedPaymentMethod== PaymentEnum.PAYTM){
                        progressStatus.postValue(START_PAYMENT_GATEWAY)
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
                    progressStatus.postValue(LOADING)
                    _mobileRechargeModel.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _mobileRechargeModel.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
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
                                    progressStatus.postValue(RECHARGE_SUCCESSFUL)
                                }
                            }else{
                                progressStatus.postValue(RECHARGE_SUCCESSFUL)

                            }

                        }
                        "FAILED"->{
                            progressStatus.postValue(RECHARGE_FAILED)
                        }
                        "PENDING"->{
                            progressStatus.postValue(PENDING)
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
                    progressStatus.postValue(LOADING)
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPaymentTransResponse.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
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
                        progressStatus.postValue(RECHARGE_FAILED)
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
                    progressStatus.postValue(LOADING)
                    _walletChargeDeducted.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletChargeDeducted.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while wallet charge deduction.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    progressStatus.postValue(RECHARGE_SUCCESSFUL)
                    _walletChargeDeducted.postValue(Resource.Success(response))
                }
        }
    }

}