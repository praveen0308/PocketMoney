package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.shopping.model.OrderListItem
import com.example.pocketmoney.shopping.model.ProductMainCategory
import com.example.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import com.example.pocketmoney.shopping.network.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepository  @Inject constructor(
        private val shoppingApiService: ShoppingApiService
) {

    suspend fun getOrderList(userId:String): Flow<List<OrderListItem>> {
        return flow {
            val response = shoppingApiService.getOrderListByUserID(userId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOrderDetails(orderNumber:String): Flow<ModelOrderDetails> {
        return flow {
            val response = shoppingApiService.getOrderDetails(orderNumber)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}

