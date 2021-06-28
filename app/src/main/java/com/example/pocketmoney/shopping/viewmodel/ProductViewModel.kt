package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.shopping.model.ProductModel
import com.example.pocketmoney.shopping.repository.ProductRepository
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
):ViewModel() {

    private val _productListBySearch = MutableLiveData<Resource<List<ProductModel>>>()
    val productListBySearch : LiveData<Resource<List<ProductModel>>> = _productListBySearch

    fun searchProduct(keyword:String) {

        viewModelScope.launch {
            productRepository
                    .searchProduct(keyword)
                    .onStart {
                        _productListBySearch.postValue(Resource.Loading(true))
                    }
                    .catch { exception ->
                        exception.message?.let {
                            _productListBySearch.postValue(Resource.Error(it))
                        }
                    }
                    .collect { charge->
                        _productListBySearch.postValue(Resource.Success(charge))
                    }
        }
    }
}

sealed class ShoppingHomeEvent{
    object GetProductList: ShoppingHomeEvent()
    object None: ShoppingHomeEvent()
}
