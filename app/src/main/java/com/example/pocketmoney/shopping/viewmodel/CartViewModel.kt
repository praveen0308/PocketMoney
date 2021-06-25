package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
        private val cartRepository: CartRepository
): ViewModel(){

    private val _addToCartOperationResult: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val addToCartOperationResult: LiveData<DataState<Boolean>>
        get() = _addToCartOperationResult

    private val _cartItemCount: MutableLiveData<DataState<Int>> = MutableLiveData()
    val cartItemCount: LiveData<DataState<Int>>
        get() = _cartItemCount

    private val _cartItems: MutableLiveData<DataState<List<CartModel>>> = MutableLiveData()
    val cartItems: LiveData<DataState<List<CartModel>>>
        get() = _cartItems

    private val _cartItemQuantity: MutableLiveData<DataState<Int>> = MutableLiveData()
    val cartItemQuantity: LiveData<DataState<Int>>
        get() = _cartItemQuantity

    val userID = cartRepository.userID.asLiveData()


    fun addToCart(itemID: Int,userID:String,quantity:Int){
        viewModelScope.launch {

            cartRepository.addCartItem(itemID,userID, quantity)
                    .onEach { dataState ->
                        _addToCartOperationResult.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getCartItemCount(userID:String){
        viewModelScope.launch {

            cartRepository.getCartItemCount(userID)
                    .onEach { dataState ->
                        _cartItemCount.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getCartItems(userID:String){
        viewModelScope.launch {

            cartRepository.getCartItems(userID)
                .onEach { dataState ->
                    _cartItems.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    fun changeCartItemQuantity(type:Int,itemID:Int,userID:String){
        viewModelScope.launch {

            cartRepository.changeItemQuantity(type, itemID, userID)
                    .onEach { dataState ->
                        _cartItemQuantity.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

}