package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.shopping.model.*
import com.example.pocketmoney.shopping.repository.StoreRepository
import com.example.pocketmoney.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
):ViewModel(){

    private val _productMainCategories = MutableLiveData<Resource<List<ProductMainCategory>>>()
    val productMainCategories : LiveData<Resource<List<ProductMainCategory>>> = _productMainCategories

    fun getProductMainCategories() {
        viewModelScope.launch {
            storeRepository
                .getMainCategories()
                .onStart {
                    _productMainCategories.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _productMainCategories.postValue(Resource.Error(it))
                    }
                }
                .collect { list->
                    _productMainCategories.postValue(Resource.Success(list))
                }

        }

    }

    private val _productCategories = MutableLiveData<Resource<List<ProductCategory>>>()
    val productCategories : LiveData<Resource<List<ProductCategory>>> = _productCategories

    fun getProductCategories() {
        viewModelScope.launch {
            storeRepository
                .getProductCategories()
                .onStart {
                    _productCategories.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _productCategories.postValue(Resource.Error(it))
                    }
                }
                .collect { list->
                    _productCategories.postValue(Resource.Success(list))
                }
        }

    }

    private val _productSubCategories = MutableLiveData<Resource<List<ProductSubCategory>>>()
    val productSubCategories : LiveData<Resource<List<ProductSubCategory>>> = _productSubCategories

    fun getProductSubCategories() {
        viewModelScope.launch {
            storeRepository
                .getSubCategories()
                .onStart {
                    _productSubCategories.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _productSubCategories.postValue(Resource.Error(it))
                    }
                }
                .collect { list->
                    _productSubCategories.postValue(Resource.Success(list))
                }
        }

    }

    private val _productBrands = MutableLiveData<Resource<List<ProductBrand>>>()
    val productBrands : LiveData<Resource<List<ProductBrand>>> = _productBrands

    fun getProductBrands() {
        viewModelScope.launch {
            storeRepository
                .getProductBrands()
                .onStart {
                    _productBrands.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _productBrands.postValue(Resource.Error(it))
                    }
                }
                .collect { list->
                    _productBrands.postValue(Resource.Success(list))
                }
        }

    }

    private val _storeOffers = MutableLiveData<Resource<List<StoreOffer>>>()
    val storeOffers : LiveData<Resource<List<StoreOffer>>> = _storeOffers

    fun getStoreOffers() {
        viewModelScope.launch {
            storeRepository
                .getStoreOffers()
                .onStart {
                    _storeOffers.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _storeOffers.postValue(Resource.Error(it))
                    }
                }
                .collect { list->
                    _storeOffers.postValue(Resource.Success(list))
                }
        }

    }

}