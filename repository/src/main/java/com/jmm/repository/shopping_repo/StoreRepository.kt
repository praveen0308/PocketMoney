package com.jmm.repository.shopping_repo

import androidx.room.withTransaction
import com.jmm.local.AppDatabase
import com.jmm.local.dao.BannerDao
import com.jmm.model.shopping_models.*
import com.jmm.network.services.StoreApiService
import com.jmm.repository.mapper.IMapper.toBannerEntity
import com.jmm.repository.mapper.IMapper.toBannerModel
import com.jmm.repository.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val storeApiService: StoreApiService,
    private val bannerDao: BannerDao,
    private val db: AppDatabase,
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

    fun getBanners() = networkBoundResource(
        query = {
            bannerDao.getAllBanners().map { it.map { a->a.toBannerModel() } }
        },
        fetch = {
            storeApiService.getStoreBanners()
        },
        saveFetchResult = { banners ->
            val bannerEntities = banners.map { it.toBannerEntity() }
            db.withTransaction {
                bannerDao.deleteAllBanners()
                bannerDao.saveBanners(bannerEntities)
            }
        }
    )
}