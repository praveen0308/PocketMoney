package com.jmm.repository

import com.google.gson.JsonObject
import com.jmm.model.TransactionDetailModel
import com.jmm.model.TransactionModel
import com.jmm.model.mlmModels.CustomerAuthBalanceResponse
import com.jmm.model.mlmModels.CustomerRequestModel1
import com.jmm.model.serviceModels.PMWalletModel
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.network.services.MLMApiService
import com.jmm.network.services.RechargeAPIService
import com.jmm.network.services.WalletService
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

    suspend fun getCustomerBalanceWithAuth(
        userId: String,
        roleId: Int
    ): Flow<CustomerAuthBalanceResponse> {
        return flow {
            val response = walletService.getCustomerAuthWithBalance(userId = userId, roleId)
            emit(response)
        }.flowOn(Dispatchers.IO)

    }
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


    suspend fun addMoneyToWallet(
    data:PaytmResponseModel
    ): Flow<Boolean> {
        return flow {
            val response = walletService.addMoneyToWallet(data)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}