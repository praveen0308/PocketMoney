package com.sampurna.pocketmoney.mlm.repository

import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.network.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaytmRepository @Inject constructor(
    private val mlmApiService: MLMApiService
){
    suspend fun initiateTransactionApi(paytmRequestData: PaytmRequestData): Flow<String> {
        return flow {
            val response = mlmApiService.initiateTransactionApi(paytmRequestData)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun addPaymentTransactionDetails(paymentGatewayTransactionModel: PaymentGatewayTransactionModel): Flow<String> {
        return flow {
            val response = mlmApiService.addPaymentTransactionDetails(paymentGatewayTransactionModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}