package com.example.pocketmoney.shopping.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import com.example.pocketmoney.mlm.model.MyPreferenceKeys
import com.example.pocketmoney.shopping.model.CartListResponse
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.ProductModel
import com.example.pocketmoney.shopping.network.ShoppingApiService
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.DataState
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


class CartRepository @Inject constructor(
        context: Context,
        private val shoppingApiService: ShoppingApiService

) {
    private val dataStore: DataStore<Preferences> = context.createDataStore(
            name = Constants.PREFERENCE_NAME
    )

    val userID: Flow<String> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                val userId = preference[MyPreferenceKeys.userId]
                userId!!
            }


    suspend fun addCartItem(itemID: Int,userID:String,quantity:Int): Flow<DataState<Boolean>> = flow{
        emit(DataState.Loading)
        try {
            val result = shoppingApiService.addToCart(itemID,userID,quantity)
            emit(DataState.Success(result))
        }catch (e: Exception){
            (DataState.Error(e))
        }
    }

    suspend fun getCartItemCount(userID:String): Flow<DataState<Int>> = flow{
        emit(DataState.Loading)
        try {
            val itemCount = shoppingApiService.getCartItemCount(userID)
            emit(DataState.Success(itemCount))
        }catch (e: Exception){
            (DataState.Error(e))
        }
    }

    suspend fun getCartItems(userID:String): Flow<DataState<List<CartModel>>> = flow{
        emit(DataState.Loading)
        try {
            val itemCount = shoppingApiService.getCartItems(userID)
            emit(DataState.Success(itemCount))
        }catch (e: Exception){
            (DataState.Error(e))
        }
    }

    suspend fun changeItemQuantity(type:Int,itemID:Int,userID:String): Flow<DataState<Int>> = flow{
        emit(DataState.Loading)
        try {
            val itemCount = shoppingApiService.quantityChange(type,itemID,userID)
            emit(DataState.Success(itemCount.toInt()))
        }catch (e: Exception){
            (DataState.Error(e))
        }
    }

}