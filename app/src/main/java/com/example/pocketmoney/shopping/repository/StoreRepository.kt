package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.shopping.model.*
import com.example.pocketmoney.shopping.network.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService
) {

    suspend fun getMainCategories(): Flow<List<ProductMainCategory>> {
        return flow {
            val response = shoppingApiService.getMainCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductCategories(): Flow<List<ProductCategory>> {
        return flow {
            val response = shoppingApiService.getCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getSubCategories(): Flow<List<ProductSubCategory>> {
        return flow {
            val response = shoppingApiService.getSubCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getProductBrands(): Flow<List<ProductBrand>> {
        return flow {
            val response = shoppingApiService.getBrandList()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getStoreOffers(): Flow<List<StoreOffer>> {
        return flow {
            val response = shoppingApiService.getOffers()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}