package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.mlm.model.TransactionModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.model.ProductListResponse
import com.example.pocketmoney.shopping.model.ProductModel
import com.example.pocketmoney.shopping.network.ShoppingApiService
import com.example.pocketmoney.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.Exception
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService
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