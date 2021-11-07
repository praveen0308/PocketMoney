package com.sampurna.pocketmoney.shopping.repository

import com.sampurna.pocketmoney.shopping.model.OrderListItem
import com.sampurna.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import com.sampurna.pocketmoney.shopping.network.OrderApiService
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepository  @Inject constructor(
        private val shoppingApiService: ShoppingApiService,
        private val orderApiService: OrderApiService
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

    suspend fun getOrderTracking(orderNumber:String): Flow<ModelOrderDetails> {
        return flow {
            val response = orderApiService.getOrderTracking(orderNumber)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun cancelOrderItem(orderNumber:String,itemId:String): Flow<Boolean> {
        return flow {
            val response = orderApiService.cancelOrderItem(orderNumber,itemId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}

