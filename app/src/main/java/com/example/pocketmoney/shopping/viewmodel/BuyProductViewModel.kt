package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.shopping.model.*
import com.example.pocketmoney.shopping.repository.BuyProductRepository
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyProductViewModel @Inject constructor(
        private val buyProductRepository: BuyProductRepository,
        private val cartRepository: CartRepository,
        private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()

    private val _productDetail: MutableLiveData<DataState<ProductModel>> = MutableLiveData()
    val productDetail: LiveData<DataState<ProductModel>>
        get() = _productDetail

    private val _similarProductList: MutableLiveData<DataState<List<ProductModel>>> = MutableLiveData()
    val similarProductList: LiveData<DataState<List<ProductModel>>>
        get() = _similarProductList

    private val _productVariants: MutableLiveData<DataState<List<ProductVariant>>> = MutableLiveData()
    val productVariants: LiveData<DataState<List<ProductVariant>>>
        get() = _productVariants

    private val _productVariantValues: MutableLiveData<DataState<List<ProductVariantValue>>> = MutableLiveData()
    val productVariantValues: LiveData<DataState<List<ProductVariantValue>>>
        get() = _productVariantValues

    fun getProductDetails(itemID: Int) {
        viewModelScope.launch {

            buyProductRepository.getProductDetail(itemID)
                    .onEach { dataState ->
                        _productDetail.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }


    fun getSimilarProducts(categoryId: Int) {
        viewModelScope.launch {
            buyProductRepository.getSimilarProductList(categoryId)
                    .onEach { dataState ->
                        _similarProductList.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getProductVariants(productId: Int) {
        viewModelScope.launch {
            buyProductRepository.getProductVariants(productId)
                    .onEach { dataState ->
                        _productVariants.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getProductVariantValues(productId: Int) {
        viewModelScope.launch {
            buyProductRepository.getProductVariantValues(productId)
                    .onEach { dataState ->
                        _productVariantValues.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }

    private val _productItemIdAcVariant: MutableLiveData<DataState<Int>> = MutableLiveData()
    val productItemIdAcVariant: LiveData<DataState<Int>>
        get() = _productItemIdAcVariant

    fun getProductItemIdAcVariant(productId: Int,variantId:String,variantValueId:String) {
        viewModelScope.launch {
            buyProductRepository.getProductItemIdAcVariant(productId, variantId, variantValueId)
                    .onEach { dataState ->
                        _productItemIdAcVariant.value = dataState
                    }
                    .launchIn(viewModelScope)
        }
    }


    private val _addToCartOperationResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val addToCartOperationResult: LiveData<Resource<Boolean>> = _addToCartOperationResult
    fun addToCart(itemID: Int,userID:String,quantity:Int){
        viewModelScope.launch {

            cartRepository.addCartItem(itemID,userID, quantity)
                .onStart {
                    _addToCartOperationResult.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addToCartOperationResult.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addToCartOperationResult.postValue(Resource.Success(response))
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


