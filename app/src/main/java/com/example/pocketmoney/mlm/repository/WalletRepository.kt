package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.TransactionModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.network.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WalletRepository @Inject constructor(
        private val mlmApiService: MLMApiService
) {

    suspend fun getWalletBalance(
            userId: String,
            roleId: Int,
            walletType: Int
    ): Flow<Double> {
     return flow {
            val response = mlmApiService.getWalletBalance(userId = userId.toDouble(), roleId, walletType)

            emit(response)
        }.flowOn(Dispatchers.IO)

    }



    suspend fun getAllTransactionHistory(requestModel: CustomerRequestModel1): Flow<List<TransactionModel>> {
        return flow {
            val response = mlmApiService.getAllTransactionHistory(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTransactionHistory(requestModel1: CustomerRequestModel1): Flow<List<TransactionModel>> {
        return flow {
            val response = mlmApiService.getTransactionHistory(requestModel1)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}