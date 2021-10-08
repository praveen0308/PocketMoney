package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.repository.CustomerRepository
import com.example.pocketmoney.mlm.repository.PaytmRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui.Recharge
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageCouponsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    val userPreferencesRepository: UserPreferencesRepository,
    private val paytmRepository: PaytmRepository
): ViewModel(){

    val userId = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val message = MutableLiveData<String>()

    val noOfCoupons = MutableLiveData(1)
    lateinit var paytmResponseModel: PaytmResponseModel
    fun incrementNoOfCoupons(){
        if (noOfCoupons.value!! < 10){
            noOfCoupons.postValue(noOfCoupons.value!!+1)
        }else{
            message.postValue("Maximum 10 coupons can be generated at one time !!!")
        }
    }
    fun decrementNoOfCoupons(){
        if (noOfCoupons.value!! > 1){
            noOfCoupons.postValue(noOfCoupons.value!!-1)
        }else{
            message.postValue("Minimum 1 coupon must be generated !!!")
        }
    }

    private val _generateCouponResponse = MutableLiveData<Resource<Int>>()
    val generateCouponResponse: LiveData<Resource<Int>> = _generateCouponResponse


    fun generateNewCoupons(userId: String,walletId:Int,count:Int) {
        viewModelScope.launch {
            customerRepository
                .generateNewCoupon(userId,walletId, count)
                .onStart {
                    _generateCouponResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _generateCouponResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _generateCouponResponse.postValue(Resource.Success(response))
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
                        _addPaymentTransResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while calling SampurnaRechargeService.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->

                    _addPaymentTransResponse.postValue(Resource.Success(response))
                }
        }
    }
}