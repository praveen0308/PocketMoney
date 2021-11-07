package com.sampurna.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.shopping.model.CartModel
import com.sampurna.pocketmoney.shopping.model.DiscountModel
import com.sampurna.pocketmoney.shopping.repository.CartRepository
import com.sampurna.pocketmoney.shopping.repository.CheckoutRepository
import com.sampurna.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val checkoutRepository: CheckoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel(){

    val couponCode = MutableLiveData("")

    val userID = userPreferencesRepository.userId.asLiveData()

    private val _cartItems: MutableLiveData<Resource<List<CartModel>>> = MutableLiveData()
    val cartItems: LiveData<Resource<List<CartModel>>> = _cartItems
    fun getCartItems(userID:String){
        viewModelScope.launch {

            cartRepository.getCartItems(userID)
                .onStart {
                    _cartItems.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _cartItems.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _cartItems.postValue(Resource.Success(response))
                }
        }
    }

    private val _cartItemQuantity: MutableLiveData<Resource<Int>> = MutableLiveData()
    val cartItemQuantity: LiveData<Resource<Int>> = _cartItemQuantity

    fun changeCartItemQuantity(type:Int,itemID:Int,userID:String){
        viewModelScope.launch {

            cartRepository.changeItemQuantity(type, itemID, userID)
                .onStart {
                    _cartItemQuantity.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _cartItemQuantity.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _cartItemQuantity.postValue(Resource.Success(response))
                }
        }
    }

    private val _isValidCoupon = MutableLiveData<Resource<Boolean>>()
    val isValidCoupon : LiveData<Resource<Boolean>> = _isValidCoupon

    fun validateCouponCode(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .validateCoupon(couponCode)
                .onStart {
                    _isValidCoupon.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isValidCoupon.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    if (response){
                        _isValidCoupon.postValue(Resource.Success(response))
                        getCouponDetails(couponCode)
                    }else{
                        _isValidCoupon.postValue(Resource.Error("Invalid coupon code !!!"))
                    }

                }
        }
    }


    private val _couponDetail = MutableLiveData<Resource<DiscountModel>>()
    val couponDetail : LiveData<Resource<DiscountModel>> = _couponDetail

    fun getCouponDetails(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .getDiscountDetails(couponCode)
                .onStart {
                    _couponDetail.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _couponDetail.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _couponDetail.postValue(Resource.Success(response))
                }
        }
    }


}