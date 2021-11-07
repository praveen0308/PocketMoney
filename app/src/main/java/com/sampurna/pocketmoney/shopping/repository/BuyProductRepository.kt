package com.sampurna.pocketmoney.shopping.repository

import com.sampurna.pocketmoney.shopping.model.*
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import com.sampurna.pocketmoney.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class BuyProductRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService
) {


    suspend fun getProductDetail(itemID: Int): Flow<DataState<ProductModel>> = flow{
        emit(DataState.Loading)
        try {
            val productModel = shoppingApiService.getProductDetail(itemID)
            emit(DataState.Success(productModel))
        }catch (e: Exception){
            (DataState.Error(e))
        }
    }

    suspend fun getSimilarProductList(categoryID:Int): Flow<DataState<List<ProductModel>>> = flow{
        emit(DataState.Loading)
        try {
            val similarProductList = shoppingApiService.getSimilarProducts(categoryID)

            emit(DataState.Success(similarProductList))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    suspend fun getProductVariants(productId:Int): Flow<DataState<List<ProductVariant>>> = flow{
        emit(DataState.Loading)
        try {
            val productVariants = shoppingApiService.getProductVariant(productId)

            emit(DataState.Success(productVariants))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    suspend fun getProductVariantValues(productId: Int): Flow<DataState<List<ProductVariantValue>>> = flow{
        emit(DataState.Loading)
        try {
            val productVariantValues = shoppingApiService.getProductVariantValues(productId)

            emit(DataState.Success(productVariantValues))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }


    suspend fun getProductItemIdAcVariant(productId: Int,variantId:String,variantValueId:String): Flow<DataState<Int>> = flow{
        emit(DataState.Loading)
        try {
            val productItemId = shoppingApiService.getProductItemIdACVariant(productId, variantId, variantValueId)

            emit(DataState.Success(productItemId))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
}