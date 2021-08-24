package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.network.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckoutRepository @Inject constructor(
        private val shoppingApiService: ShoppingApiService
) {


    suspend fun createCustomerOrder(
        customerOrder: CustomerOrder
    ): Flow<String> {
        return flow {
            val response = shoppingApiService.createCustomerOrder(customerOrder)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updatePaymentStatus(orderNumber: String,paymentStatusId:Int): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.updatePaymentStatus(orderNumber, paymentStatusId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


}