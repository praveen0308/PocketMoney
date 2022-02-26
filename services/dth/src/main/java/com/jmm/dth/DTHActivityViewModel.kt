package com.jmm.dth

import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.jmm.model.DthCustomerDetail
import com.jmm.model.ModelOperator
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.serviceModels.RechargeHistoryModel
import com.jmm.repository.*
import com.jmm.util.Resource
import com.jmm.util.identify
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
    private val paytmRepository: PaytmRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    var rechargeAmount = MutableLiveData<Int>()
    val currentActivePage = MutableLiveData(0)
    val rechargeMobileNo  = MutableLiveData<String>()
    val rechargeMobileNumber  = MutableLiveData<String>()

    val selectedOperator = MutableLiveData<ModelOperator>()
    lateinit var recharge : MobileRechargeModel

    val dthRechargePageState : MutableLiveData<DthRechargePageState> = MutableLiveData(DthRechargePageState.Initial)
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

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    dthRechargePageState.postValue(DthRechargePageState.Processing("Initiating transaction..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        dthRechargePageState.postValue(DthRechargePageState.Error(exception.identify()))
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    dthRechargePageState.postValue(DthRechargePageState.ReceivedChecksum(response))
                }
        }
    }

    fun callDthRechargeService(paytmResponseModel: PaytmResponseModel?) {
        viewModelScope.launch {
            serviceRepository
                .callNewDthRechargeService(recharge,paytmResponseModel)
                .onStart {
                    dthRechargePageState.postValue(DthRechargePageState.Processing("Processing..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        dthRechargePageState.postValue(DthRechargePageState.Error(exception.identify()))
                        Timber.d("Error caused by >>> mobileRechargeWithWallet")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    if (response.IsValidated){
                        dthRechargePageState.postValue(DthRechargePageState.OnRechargeResponseReceived(response))
                    }else{
                        dthRechargePageState.postValue(DthRechargePageState.InsufficientBalance)
                    }
                }
        }

    }

}


sealed class DthRechargePageState{
    object Initial : DthRechargePageState()
    object Loading : DthRechargePageState()
    data class Error(val msg:String) : DthRechargePageState()
    data class Processing(val msg:String) : DthRechargePageState()

    object InsufficientBalance : DthRechargePageState()

    object InitiatingTransaction : DthRechargePageState()
    data class ReceivedChecksum(val checksum:String) : DthRechargePageState()
    object RequestingGateway : DthRechargePageState()
    object CancelledGateway : DthRechargePageState()
    data class ReceivedGatewayResponse(val paytmResponseModel: PaytmResponseModel) : DthRechargePageState()

    data class OnRechargeResponseReceived(val response: MobileRechargeModel) : DthRechargePageState()

}