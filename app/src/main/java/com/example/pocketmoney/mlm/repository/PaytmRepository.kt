package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.network.MLMApiService
import com.example.pocketmoney.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

}