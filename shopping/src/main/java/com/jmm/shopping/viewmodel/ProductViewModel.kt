package com.jmm.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.shopping_models.ProductModel
import com.jmm.repository.shopping_repo.ProductRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
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
