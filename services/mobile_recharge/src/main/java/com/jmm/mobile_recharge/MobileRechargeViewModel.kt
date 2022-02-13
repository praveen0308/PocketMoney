package com.jmm.mobile_recharge

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.jmm.core.utils.getMobileOperatorCode
import com.jmm.mobile_recharge.Recharge.Companion.CHECKING_WALLET_BALANCE
import com.jmm.mobile_recharge.Recharge.Companion.CHECKSUM_RECEIVED
import com.jmm.mobile_recharge.Recharge.Companion.ERROR
import com.jmm.mobile_recharge.Recharge.Companion.INITIATING_RECHARGE_SERVICE
import com.jmm.mobile_recharge.Recharge.Companion.INITIATING_TRANSACTION
import com.jmm.mobile_recharge.Recharge.Companion.INSUFFICIENT_BALANCE
import com.jmm.mobile_recharge.Recharge.Companion.PENDING
import com.jmm.mobile_recharge.Recharge.Companion.PROCESSING
import com.jmm.mobile_recharge.Recharge.Companion.RECHARGE_FAILED
import com.jmm.mobile_recharge.Recharge.Companion.RECHARGE_SUCCESSFUL
import com.jmm.model.ModelContact
import com.jmm.model.ModelOperator
import com.jmm.model.RechargeEnum
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.*
import com.jmm.repository.*
import com.jmm.util.DataState
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MobileRechargeViewModel @Inject constructor(
    private val rechargeRepository: RechargeRepository,
    private val walletRepository: WalletRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val serviceRepository: ServiceRepository,
    private val paytmRepository: PaytmRepository
) :ViewModel() {

    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val currentActivePage = MutableLiveData(0)

    val selectedPaymentMethod = MutableLiveData(PaymentEnum.WALLET)
    var rechargeAmount = MutableLiveData<Int>()

    var requestId = ""

    val rechargeMobileNo  = MutableLiveData<String>()
    val rechargeMobileNumber  = MutableLiveData<String>()
    val selectedOperator = MutableLiveData<String>()
    val selectedCircle  = MutableLiveData("Maharashtra")
    lateinit var recharge : MobileRechargeModel
    val progressStatus = MutableLiveData<Int>()

    var transactionToken = ""
    lateinit var paytmResponseModel: PaytmResponseModel

    lateinit var rechargeApiResponse : MobileRechargeModel



    private val _contactList: MutableLiveData<DataState<List<ModelContact>>> = MutableLiveData()
    val contactList: LiveData<DataState<List<ModelContact>>>
        get() = _contactList

//    val progressStatus = MutableLiveData(false)

    fun getContactList(){
        viewModelScope.launch {
            rechargeRepository.getContactList()
                    .onEach { dataState ->
                        _contactList.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    val selectedContact = MutableLiveData<ModelContact>()

    private val _operatorList: MutableLiveData<DataState<List<ModelOperator>>> = MutableLiveData()
    val operatorList: LiveData<DataState<List<ModelOperator>>>
        get() = _operatorList

    fun getOperatorList(operatorOf:String){
        viewModelScope.launch {

            rechargeRepository.getOperatorList(operatorOf)
                    .onEach { dataState ->
                        _operatorList.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

//    val selectedMobileOperator = MutableLiveData<ModelOperator>()

    private val _mobileOperators = MutableLiveData<List<ModelOperator>>()
    val mobileOperators: LiveData<List<ModelOperator>> = _mobileOperators

    fun getMobileOperators() {
        viewModelScope.launch {
            _mobileOperators.postValue(rechargeRepository.getOperators(RechargeEnum.PREPAID))

        }
    }

    private val _circleNOperatorOfMobileNo = MutableLiveData<Resource<MobileCircleOperator>>()
    val circleNOperatorOfMobileNo: LiveData<Resource<MobileCircleOperator>> = _circleNOperatorOfMobileNo


    fun getCircleNOperatorOfMobileNo(mobileNo:String) {
        viewModelScope.launch {
            rechargeRepository
                    .getOperatorNCircleOfMobileNo(mobileNo)
                    .onStart {
                        _circleNOperatorOfMobileNo.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _circleNOperatorOfMobileNo.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _circleNOperatorOfMobileNo.postValue(Resource.Success(response))
                    }
        }

    }

    private val _mobileSimplePlanList = MutableLiveData<Resource<SimplePlanResponse>>()
    val mobileSimplePlanList: LiveData<Resource<SimplePlanResponse>> = _mobileSimplePlanList
    fun getMobileSimplePlanList(circle: String, mobileOperator:String) {
        viewModelScope.launch {
            rechargeRepository
                    .getMobileSimplePlans(circle,mobileOperator)
                    .onStart {
                        _mobileSimplePlanList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _mobileSimplePlanList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _mobileSimplePlanList.postValue(Resource.Success(response))
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

    private val _mobileSpecialPlanList = MutableLiveData<Resource<List<MobileOperatorPlan>>>()
    val mobileSpecialPlanList: LiveData<Resource<List<MobileOperatorPlan>>> = _mobileSpecialPlanList

    fun getMobileSpecialPlanList(mobileNo: String, mobileOperator:String) {
        viewModelScope.launch {
            rechargeRepository
                    .getMobileSpecialPlans(mobileNo,mobileOperator)
                    .onStart {
                        _mobileSpecialPlanList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _mobileSpecialPlanList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _mobileSpecialPlanList.postValue(Resource.Success(response))
                    }
        }

    }

    private val _mobileServiceCircleList = MutableLiveData<Resource<List<IdNameModel>>>()
    val mobileServiceCircleList: LiveData<Resource<List<IdNameModel>>> = _mobileServiceCircleList

    fun getMobileServiceCircleList(providerID:Int=1) {
        viewModelScope.launch {
            rechargeRepository
                    .getMobileServiceCircle(providerID)
                    .onStart {
                        _mobileServiceCircleList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _mobileServiceCircleList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _mobileServiceCircleList.postValue(Resource.Success(response))
                    }
        }

    }



    private val _mobileServiceOperatorList = MutableLiveData<Resource<List<IdNameModel>>>()
    val mobileServiceOperatorList: LiveData<Resource<List<IdNameModel>>> = _mobileServiceOperatorList

    fun getMobileServiceOperatorList(serviceTypeId: Int=1,serviceProviderId: Int=1, circleCode: String?=null) {
        viewModelScope.launch {
            rechargeRepository
                    .getMobileServiceOperators(serviceTypeId,serviceProviderId,circleCode)
                    .onStart {
                        _mobileServiceOperatorList.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _mobileServiceOperatorList.postValue(Resource.Error(it))
                        }
                    }
                    .collect { response->
                        _mobileServiceOperatorList.postValue(Resource.Success(response))
                    }
        }

    }

    private val _selectedRechargePlan = MutableLiveData<MobileOperatorPlan>()
    val selectedRechargePlan: LiveData<MobileOperatorPlan> = _selectedRechargePlan


    fun setSelectedMobileOperatorPlan(plan: MobileOperatorPlan){
        _selectedRechargePlan.postValue(plan)
    }

    fun getSelectedRechargePlan(): MobileOperatorPlan {
        return _selectedRechargePlan.value!!
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
                        recharge.ServiceTypeID = 1
                        recharge.WalletTypeID = WalletType.Wallet.id
                        recharge.OperatorCode = getMobileOperatorCode(selectedOperator.value!!).toString()
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 9

                        addUsedServiceDetail(recharge)

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
                        recharge.ServiceTypeID = 1
                        recharge.WalletTypeID = WalletType.PCash.id
                        recharge.OperatorCode = getMobileOperatorCode(selectedOperator.value!!).toString()
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 9

                        addUsedServiceDetail(recharge)
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
                    progressStatus.postValue(INITIATING_TRANSACTION)
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
                    progressStatus.postValue(INITIATING_RECHARGE_SERVICE)
                    _addUsedServiceDetailResponse.postValue(Resource.Loading(true))
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
                    if (serviceRepository.selectedPaymentMethod==PaymentEnum.PAYTM){
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
                        callSampurnaRechargeService(recharge)
                    }
                }
        }

    }


    // step 3
    private val _mobileRechargeModel = MutableLiveData<Resource<MobileRechargeModel>>()
    val mobileRechargeModel: LiveData<Resource<MobileRechargeModel>> = _mobileRechargeModel

    fun callSampurnaRechargeService(mobileRechargeModel: MobileRechargeModel) {
        viewModelScope.launch {
            serviceRepository
                .callSamupurnaRechargeService(mobileRechargeModel)
                .onStart {
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
                    rechargeApiResponse = response
                    when(response.Status){
                        "SUCCESS"->{
                            if (serviceRepository.selectedPaymentMethod==PaymentEnum.PAYTM){
                                if (paytmResponseModel.PAYMENTMODE != "UPI"){
                                    val amountDeduct = (paytmResponseModel.TXNAMOUNT?.toDouble() ?: 0.0) * 2 / 100
                                    walletChargeDeduct(mobileRechargeModel.UserID!!,WalletType.PCash.id,amountDeduct,recharge.RequestID!!,18)
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
                    progressStatus.postValue(PROCESSING)
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
                        callSampurnaRechargeService(recharge)
                        _addPaymentTransResponse.postValue(Resource.Success(response))
                    }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                        Timber.d("Payment Gateway response was failed.")
//                        progressStatus.postValue(INITIATING_RECHARGE_SERVICE)
                        progressStatus.postValue(RECHARGE_FAILED)
                        _addPaymentTransResponse.postValue(Resource.Error("Payment failed !!!"))

                    }

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
                    progressStatus.postValue(PROCESSING)
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
