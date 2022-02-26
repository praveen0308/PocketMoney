package com.jmm.mobile_recharge

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.jmm.model.ModelContact
import com.jmm.model.ModelOperator
import com.jmm.model.serviceModels.*
import com.jmm.repository.*
import com.jmm.util.DataState
import com.jmm.util.Resource
import com.jmm.util.identify
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

    var rechargeAmount = MutableLiveData<Int>()

    val rechargeMobileNo  = MutableLiveData<String>()
    val rechargeMobileNumber  = MutableLiveData<String>()
    val selectedOperator = MutableLiveData<String>()
    val selectedCircle  = MutableLiveData("Maharashtra")
    lateinit var recharge : MobileRechargeModel

    val rechargePageState : MutableLiveData<MobileRechargePageState> = MutableLiveData(MobileRechargePageState.Initial)

    private val _contactList: MutableLiveData<DataState<List<ModelContact>>> = MutableLiveData()
    val contactList: LiveData<DataState<List<ModelContact>>>
        get() = _contactList

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
            _mobileOperators.postValue(getOperators())

        }
    }

    private fun getOperators(): List<ModelOperator> {
        val mobileOperatorList:MutableList<ModelOperator> = ArrayList()

        mobileOperatorList.add(ModelOperator("Jio", R.drawable.ic_jio))
        mobileOperatorList.add(ModelOperator("Airtel",R.drawable.ic_airtel))
        mobileOperatorList.add(ModelOperator("VI",R.drawable.ic_vi_vodafone_idea))
        mobileOperatorList.add(ModelOperator("Idea",R.drawable.ic_vi_vodafone_idea))
        mobileOperatorList.add(ModelOperator("Vodafone",R.drawable.ic_vi_vodafone_idea))

        mobileOperatorList.add(ModelOperator("Tata Docomo",R.drawable.ic_tata_docomo))
        mobileOperatorList.add(ModelOperator("Docomo Special",R.drawable.ic_tata_docomo))

        mobileOperatorList.add(ModelOperator("BSNL",R.drawable.ic_bsnl))
        mobileOperatorList.add(ModelOperator("BSNL Special",R.drawable.ic_bsnl))

        mobileOperatorList.add(ModelOperator("MTNL",R.drawable.ic_mtnl))
        mobileOperatorList.add(ModelOperator("MTNL Mumbai Special",R.drawable.ic_mtnl))
        mobileOperatorList.add(ModelOperator("MTNL Mumbai Topup",R.drawable.ic_mtnl))

        return mobileOperatorList
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

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    rechargePageState.postValue(MobileRechargePageState.Processing("Initiating transaction..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        rechargePageState.postValue(MobileRechargePageState.Error(exception.identify()))
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    rechargePageState.postValue(MobileRechargePageState.ReceivedChecksum(response))
                }
        }
    }

    fun callMobileRechargeService(paytmResponseModel: PaytmResponseModel?) {
        viewModelScope.launch {
            serviceRepository
                .callNewMobileRechargeService(recharge,paytmResponseModel)
                .onStart {
                    rechargePageState.postValue(MobileRechargePageState.Processing("Processing..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        rechargePageState.postValue(MobileRechargePageState.Error(exception.identify()))
                        Timber.d("Error caused by >>> mobileRechargeWithWallet")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    if (response.IsValidated){
                        rechargePageState.postValue(MobileRechargePageState.OnRechargeResponseReceived(response))
                    }else{
                        rechargePageState.postValue(MobileRechargePageState.InsufficientBalance)
                    }
                }
        }

    }


}

sealed class MobileRechargePageState{
    object Initial : MobileRechargePageState()
    object Loading : MobileRechargePageState()
    data class Error(val msg:String) : MobileRechargePageState()
    data class Processing(val msg:String) : MobileRechargePageState()

    object InsufficientBalance : MobileRechargePageState()

    object InitiatingTransaction : MobileRechargePageState()
    data class ReceivedChecksum(val checksum:String) : MobileRechargePageState()
    object RequestingGateway : MobileRechargePageState()
    object CancelledGateway : MobileRechargePageState()
    data class ReceivedGatewayResponse(val paytmResponseModel: PaytmResponseModel) : MobileRechargePageState()

    data class OnRechargeResponseReceived(val response: MobileRechargeModel) : MobileRechargePageState()
   /* data class RechargeSuccessful(val response: MobileRechargeModel) : MobileRechargePageState()
    data class RechargeFailed(val response: MobileRechargeModel) : MobileRechargePageState()
    data class RechargePending(val response: MobileRechargeModel) : MobileRechargePageState()*/
}
