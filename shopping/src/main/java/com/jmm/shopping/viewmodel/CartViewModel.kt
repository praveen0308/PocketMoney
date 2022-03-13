package com.jmm.shopping.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.model.shopping_models.CartModel
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
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val checkoutRepository: CheckoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {


    val userID = userPreferencesRepository.userId.asLiveData()

    val pageState: MutableLiveData<CartPageState> = MutableLiveData(CartPageState.Idle)

    fun getCartItems(userID: String) {
        viewModelScope.launch {

            cartRepository.getCartItems(userID)
                .onStart {
                    pageState.postValue(CartPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CartPageState.Error(exception.identify()))
                    }
                }
                .collect { cartItems ->
                    if (cartItems.isEmpty()) pageState.postValue(CartPageState.EmptyCart)
                    else{
                        pageState.postValue(CartPageState.ReceivedCartItems(cartItems))
//                        checkoutRepository.populatePrices(cartItems)
                    }

                }
        }
    }

    fun changeCartItemQuantity(type: Int, itemID: Int, userID: String) {
        viewModelScope.launch {

            cartRepository.changeItemQuantity(type, itemID, userID)
                .onStart {
                    pageState.postValue(CartPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CartPageState.Error(exception.identify()))
                    }
                }
                .collect {
                    getCartItems(userID)
                }
        }
    }

}

sealed class CartPageState {
    object Idle : CartPageState()
    object Loading : CartPageState()
    object EmptyCart : CartPageState()
    data class ReceivedCartItems(val cartItems: List<CartModel>) : CartPageState()
    data class Error(val msg: String) : CartPageState()
}