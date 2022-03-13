package com.jmm.repository.shopping_repo

import androidx.room.withTransaction
import com.jmm.local.AppDatabase
import com.jmm.local.dao.ProductDao
import com.jmm.model.shopping_models.ProductModel
import com.jmm.network.services.ShoppingApiService
import com.jmm.repository.mapper.IMapper.toProductEntity
import com.jmm.repository.mapper.IMapper.toProductModel
import com.jmm.repository.networkBoundResource
import com.jmm.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService,
    private val appDatabase: AppDatabase,
    private val productDao: ProductDao
) {

//    suspend fun getHomeProductList(): Flow<DataState<List<ProductModel>>> = flow{
//        emit(DataState.Loading)
//        try {
//            val productList = shoppingApiService.getProductList()
//
//            emit(DataState.Success(productList))
//        }catch (e: Exception){
//            emit(DataState.Error(e))
//        }
//    }

    fun getProducts() = networkBoundResource(
        query = {
            productDao.getAllProducts().map { it.map { a->a.toProductModel() } }
        },
        fetch = {
            shoppingApiService.getProductList()
        },
        saveFetchResult = { banners ->
            val productEntities = banners.map { it.toProductEntity() }
            appDatabase.withTransaction {
                productDao.deleteAllProducts()
                productDao.saveProducts(productEntities)
            }
        }
    )
    suspend fun getHomeProductList(): Flow<List<ProductModel>> {
        return flow {
            val response = shoppingApiService.getProductList()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }



    suspend fun getHomePageContent(): Flow<DataState<ProductModel>> = flow{
        emit(DataState.Loading)
        try {
            val productList = shoppingApiService.getProductList()
            productList.asFlow().onEach { emit(DataState.Success(it)) }

        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }


    suspend fun searchProduct(
            keyword: String
    ): Flow<List<ProductModel>> {
        return flow {
            val response = shoppingApiService.getSearchProduct(keyword)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}