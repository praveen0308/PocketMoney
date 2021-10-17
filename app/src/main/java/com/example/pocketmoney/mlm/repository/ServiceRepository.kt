package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.RechargeHistoryModel
import com.example.pocketmoney.mlm.model.serviceModels.UsedServiceDetailModel
import com.example.pocketmoney.mlm.network.RechargeAPIService
import com.example.pocketmoney.mlm.ui.payouts.WalletToPaytmTransfer
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val rechargeAPIService: RechargeAPIService
){
    var selectedPaymentMethod = PaymentEnum.WALLET

    suspend fun addUsedServiceDetail(usedServiceDetailModel: MobileRechargeModel): Flow<Int> {
        return flow {
            val response = rechargeAPIService.addUsedServiceDetail(usedServiceDetailModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUsedServiceHistory(jsonObject: JsonObject): Flow<List<RechargeHistoryModel>> {
        return flow {
            val response = rechargeAPIService.getUsedServiceHistory(jsonObject)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUsedServiceRequestId(userId:String,mobileNo:String): Flow<String> {
        return flow {
            val response = rechargeAPIService.getServiceUsedRequestId(userId,mobileNo)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun callSamupurnaRechargeService(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.callMobileRechargeServiceSampurnaApi(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun callSamupurnaDthRechargeService(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.callDTHRechargeServiceSampurnaApi(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}