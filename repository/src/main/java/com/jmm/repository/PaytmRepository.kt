package com.jmm.repository

import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.network.services.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaytmRepository @Inject constructor(
    private val mlmApiService: MLMApiService
){
    suspend fun initiateTransactionApi(paytmRequestData: PaytmRequestData,isStaging:Boolean=false): Flow<String> {
        return flow {
            val response = if (isStaging) mlmApiService.initiateTransactionStagingApi(paytmRequestData)
                else mlmApiService.initiateTransactionApi(paytmRequestData)

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