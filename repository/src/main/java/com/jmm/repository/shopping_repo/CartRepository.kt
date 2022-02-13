package com.jmm.repository.shopping_repo

import com.jmm.model.shopping_models.CartModel
import com.jmm.network.services.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class CartRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService
) {

//    suspend fun addCartItem(itemID: Int,userID:String,quantity:Int): Flow<DataState<Boolean>> = flow{
//        emit(DataState.Loading)
//        try {
//            val result = shoppingApiService.addToCart(itemID,userID,quantity)
//            emit(DataState.Success(result))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun addCartItem(itemID: Int, userID: String, quantity: Int): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.addToCart(itemID, userID, quantity)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

//    suspend fun getCartItemCount(userID:String): Flow<DataState<Int>> = flow{
//        emit(DataState.Loading)
//        try {
//            val itemCount = shoppingApiService.getCartItemCount(userID)
//            emit(DataState.Success(itemCount))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun getCartItemsCount(userID: String): Flow<Int> {
        return flow {
            val response = shoppingApiService.getCartItemCount(userID)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


//    suspend fun getCartItems(userID:String): Flow<DataState<List<CartModel>>> = flow{
//        emit(DataState.Loading)
//        try {
//            val itemCount = shoppingApiService.getCartItems(userID)
//            emit(DataState.Success(itemCount))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun getCartItems(userID: String): Flow<List<CartModel>> {
        return flow {
            val response = shoppingApiService.getCartItems(userID)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
//
//    suspend fun changeItemQuantity(type:Int,itemID:Int,userID:String): Flow<DataState<Int>> = flow{
//        emit(DataState.Loading)
//        try {
//            val itemCount = shoppingApiService.quantityChange(type,itemID,userID)
//            emit(DataState.Success(itemCount.toInt()))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun changeItemQuantity(type: Int, itemID: Int, userID: String): Flow<Int> {
        return flow {
            val response = shoppingApiService.quantityChange(type, itemID, userID).toInt()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}