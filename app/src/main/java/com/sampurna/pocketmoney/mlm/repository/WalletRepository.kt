package com.sampurna.pocketmoney.mlm.repository

import com.sampurna.pocketmoney.mlm.model.TransactionDetailModel
import com.sampurna.pocketmoney.mlm.model.TransactionModel
import com.sampurna.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.sampurna.pocketmoney.mlm.model.serviceModels.PMWalletModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.network.MLMApiService
import com.sampurna.pocketmoney.mlm.network.RechargeAPIService
import com.sampurna.pocketmoney.mlm.network.WalletService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WalletRepository @Inject constructor(
        private val mlmApiService: MLMApiService,
        private val rechargeAPIService: RechargeAPIService,
        private val walletService: WalletService
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


    suspend fun viewTransactionDetail(transactionId:String): Flow<TransactionDetailModel> {
        return flow {
            val response = walletService.viewTransactionDetails(transactionId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun transferB2BWallet(requestData : JsonObject): Flow<Int> {
        return flow {
            val response = mlmApiService.b2bWalletTransfer(requestData)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addCustomerWalletDetails(pmWalletModel: PMWalletModel): Flow<String> {
        return flow {
            val response = mlmApiService.addCustomerWalletDetails(pmWalletModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addPaymentTransactionDetails(paymentGatewayTransactionModel: PaymentGatewayTransactionModel): Flow<String> {
        return flow {
            val response = mlmApiService.addPaymentTransactionDetails(paymentGatewayTransactionModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun actionOnWalletRequest(
        requestId:String,
        comment:String,
        status:String,
        paymentMode:String
    ): Flow<Int> {
        return flow {
            val response = mlmApiService.actionOnWalletRequest(requestId, comment, status, paymentMode)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addCompanyTransactionDetail(
        transferBy:String,
        userId: String,
        amount:Double,
        walletType: Int,
        transType: Int,
        referenceId: String,
        action:String
    ): Flow<Int> {
        return flow {
            val response = mlmApiService.addCompanyTransaction(transferBy,userId,amount,
                walletType,transType, referenceId, action)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun walletChargeDeduction(
        userId: String,
        walletId:Int,
        amount:Double,
        requestId: String,
        serviceId:Int
    ): Flow<Int> {
        return flow {
            val response = rechargeAPIService.walletChargeDeduction(userId, walletId, amount, requestId, serviceId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}