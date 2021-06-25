package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.example.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.example.pocketmoney.mlm.model.serviceModels.MobileCircleOperator
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.SimplePlanResponse
import com.example.pocketmoney.mlm.repository.RechargeRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RechargeViewModel @Inject constructor(
        private val rechargeRepository: RechargeRepository
) :ViewModel() {

    private val _contactList: MutableLiveData<DataState<List<ModelContact>>> = MutableLiveData()
    val contactList: LiveData<DataState<List<ModelContact>>>
        get() = _contactList

    private val _operatorList: MutableLiveData<DataState<List<ModelOperator>>> = MutableLiveData()
    val operatorList: LiveData<DataState<List<ModelOperator>>>
        get() = _operatorList

    fun getContactList(){
        viewModelScope.launch {

            rechargeRepository.getContactList()
                    .onEach { dataState ->
                        _contactList.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getOperatorList(operatorOf:String){
        viewModelScope.launch {

            rechargeRepository.getOperatorList(operatorOf)
                    .onEach { dataState ->
                        _operatorList.value = dataState
                    }
                    .launchIn(viewModelScope)
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
}