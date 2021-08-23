package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.UsedServiceDetailModel
import com.example.pocketmoney.mlm.network.RechargeAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val rechargeAPIService: RechargeAPIService
){
    suspend fun addUsedServiceDetail(usedServiceDetailModel: UsedServiceDetailModel): Flow<Int> {
        return flow {
            val response = rechargeAPIService.addUsedServiceDetail(usedServiceDetailModel)
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
}