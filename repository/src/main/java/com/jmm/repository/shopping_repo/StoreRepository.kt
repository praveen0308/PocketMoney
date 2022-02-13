package com.jmm.repository.shopping_repo

import com.jmm.model.shopping_models.*
import com.jmm.network.services.StoreApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val storeApiService: StoreApiService
) {

    suspend fun getMainCategories(): Flow<List<ProductMainCategory>> {
        return flow {
            val response = storeApiService.getMainCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductCategories(): Flow<List<ProductCategory>> {
        return flow {
            val response = storeApiService.getCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getSubCategories(): Flow<List<ProductSubCategory>> {
        return flow {
            val response = storeApiService.getSubCategories()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getProductBrands(): Flow<List<ProductBrand>> {
        return flow {
            val response = storeApiService.getBrandList()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getStoreOffers(): Flow<List<StoreOffer>> {
        return flow {
            val response = storeApiService.getOffers()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStoreBanners(): Flow<List<BannerModel>> {
        return flow {
            val response = storeApiService.getStoreBanners()
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}