package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.example.pocketmoney.mlm.model.serviceModels.*
import com.example.pocketmoney.mlm.repository.*
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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


    val selectedPaymentMethod = MutableLiveData(PaymentEnum.WALLET)
    var rechargeAmount = MutableLiveData<Int>()

    var requestId = ""

    val rechargeMobileNo  = MutableLiveData<String>()
    val selectedOperator = MutableLiveData<String>()
    val selectedCircle  = MutableLiveData<String>("Mumbai")

    private val _contactList: MutableLiveData<DataState<List<ModelContact>>> = MutableLiveData()
    val contactList: LiveData<DataState<List<ModelContact>>>
        get() = _contactList

    val progressStatus = MutableLiveData(false)

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


    fun getSelectedRechargePlan():MobileOperatorPlan{
        return _selectedRechargePlan.value!!
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

//    step 1
    private val _addUsedServiceDetailResponse = MutableLiveData<Resource<Int>>()
    val addUsedServiceDetailResponse: LiveData<Resource<Int>> = _addUsedServiceDetailResponse


    fun addUsedServiceDetail(usedServiceDetailModel: MobileRechargeModel) {

        viewModelScope.launch {

            serviceRepository
                .addUsedServiceDetail(usedServiceDetailModel)
                .onStart {
                    _addUsedServiceDetailResponse.postValue(Resource.Loading(true))
                    progressStatus.postValue(true)
                }
                .catch { exception ->
                    exception.message?.let {
                        _addUsedServiceDetailResponse.postValue(Resource.Error(it))

                    }
                }
                .collect { response->
                    if (response>0){
                        getUsedServiceRequestId(userId.value!!,rechargeMobileNo.value!!)
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
                        _usedServiceRequestId.postValue(Resource.Error(it))
                    }
                }
                .collect { response->

                    _usedServiceRequestId.postValue(Resource.Success(response))
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
                        _mobileRechargeModel.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
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


    private val _walletChargeDeducted = MutableLiveData<Resource<Int>>()
    val walletChargeDeducted : LiveData<Resource<Int>> = _walletChargeDeducted

    fun walletChargeDeduct(userId: String,walletId:Int,amount:Double,requestId:String,serviceId:Int) {

        viewModelScope.launch {
            walletRepository
                .walletChargeDeduction(userId, walletId, amount, requestId, serviceId)
                .onStart {
                    _walletChargeDeducted.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletChargeDeducted.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _walletChargeDeducted.postValue(Resource.Success(response))
                }
        }
    }

}