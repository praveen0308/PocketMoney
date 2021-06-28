package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageCouponsViewModel @Inject constructor(

): ViewModel(){

    val message = MutableLiveData<String>()

    val noOfCoupons = MutableLiveData(1)

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

    fun getNoOfCoupons():Int = noOfCoupons.value!!
}