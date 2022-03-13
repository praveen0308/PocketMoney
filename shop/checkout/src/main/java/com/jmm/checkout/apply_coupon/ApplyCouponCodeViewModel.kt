package com.jmm.checkout.apply_coupon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.shopping_models.DiscountModel
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.shopping_repo.CartRepository
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ApplyCouponCodeViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val checkoutRepository: CheckoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel(){

    val couponCode = MutableLiveData("")
    val pageState :MutableLiveData<ApplyCouponCodePageState> = MutableLiveData(ApplyCouponCodePageState.Idle)

    fun validateCouponCode(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .validateCoupon(couponCode)
                .onStart {
                    pageState.postValue(ApplyCouponCodePageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ApplyCouponCodePageState.Error(exception.identify()))

                    }
                }
                .collect { response->
                    if (response){
                        pageState.postValue(ApplyCouponCodePageState.CouponValid)
                        getCouponDetails(couponCode)
                    }else{
                        pageState.postValue(ApplyCouponCodePageState.CouponNotValid)
                    }

                }
        }
    }

    private fun getCouponDetails(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .getDiscountDetails(couponCode)
                .onStart {
                    pageState.postValue(ApplyCouponCodePageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(ApplyCouponCodePageState.Error(exception.identify()))
                    }
                }
                .collect { response->
                    pageState.postValue(ApplyCouponCodePageState.ReceivedCouponDetails(response))
                }
        }
    }


}


sealed class ApplyCouponCodePageState{
    object Idle : ApplyCouponCodePageState()
    object Loading : ApplyCouponCodePageState()
    object CouponValid : ApplyCouponCodePageState()
    object CouponNotValid : ApplyCouponCodePageState()
    data class ReceivedCouponDetails(val discountCoupon:DiscountModel) : ApplyCouponCodePageState()
    data class Error(val msg:String) : ApplyCouponCodePageState()

}