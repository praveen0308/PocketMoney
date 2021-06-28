package com.example.pocketmoney.mlm.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.repository.AccountRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.shopping.model.ProductModel
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.shopping.repository.ProductRepository
import com.example.pocketmoney.shopping.viewmodel.ShoppingHomeEvent
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    private val _productList: MutableLiveData<Resource<List<ProductModel>>> = MutableLiveData()
    val productList: LiveData<Resource<List<ProductModel>>> = _productList

    fun getProductList(shoppingHomeEvent: ShoppingHomeEvent){
        viewModelScope.launch {
            productRepository
                .getHomeProductList()
                .onStart {
                    _productList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _productList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _productList.postValue(Resource.Success(response))
                }
        }

    }
    private val _cartItemCount: MutableLiveData<Resource<Int>> = MutableLiveData()
    val cartItemCount: LiveData<Resource<Int>> = _cartItemCount

    fun getCartItemCount(userID:String){
        viewModelScope.launch {

            cartRepository.getCartItemsCount(userID)
                .onStart {
                    _cartItemCount.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _cartItemCount.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _cartItemCount.postValue(Resource.Success(response))
                }
        }
    }


}