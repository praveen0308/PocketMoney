package com.jmm.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.serviceModels.RechargeHistoryModel
import com.jmm.network.services.RechargeAPIService
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

    suspend fun callSamupurnaPlayRechargeService(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.callPlayRechargeServiceSampurnaApi(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun callSamupurnaDthRechargeService(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.callDTHRechargeServiceSampurnaApi(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun mobileRechargeWithWallet(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.onlineMobileRechargeWithWallet(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun callNewMobileRechargeService(mobileRechargeModel: MobileRechargeModel,paytmResponseModel: PaytmResponseModel?): Flow<MobileRechargeModel> {
        return flow {
            if (paytmResponseModel==null){
                val response = rechargeAPIService.onlineMobileRechargeWithWallet(mobileRechargeModel)
                emit(response)
            }else{
                val onlineRechargeModel = JsonObject()
                onlineRechargeModel.add("MobileRechargeModel",Gson().toJsonTree(mobileRechargeModel))
                onlineRechargeModel.add("PaytmResponseData",Gson().toJsonTree(paytmResponseModel))
                val response = rechargeAPIService.onlineMobileRechargeWithGateway(onlineRechargeModel)
                emit(response)
            }


        }.flowOn(Dispatchers.IO)
    }

    suspend fun dthRechargeWithWallet(mobileRechargeModel: MobileRechargeModel): Flow<MobileRechargeModel> {
        return flow {
            val response = rechargeAPIService.onlineDthWithWallet(mobileRechargeModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }



    suspend fun callNewDthRechargeService(mobileRechargeModel: MobileRechargeModel,paytmResponseModel: PaytmResponseModel?): Flow<MobileRechargeModel> {
        return flow {
            if (paytmResponseModel==null){
                val response = rechargeAPIService.onlineDthWithWallet(mobileRechargeModel)
                emit(response)
            }else{
                val onlineRechargeModel = JsonObject()
                onlineRechargeModel.add("MobileRechargeModel",Gson().toJsonTree(mobileRechargeModel))
                onlineRechargeModel.add("PaytmResponseData",Gson().toJsonTree(paytmResponseModel))
                val response = rechargeAPIService.onlineDthWithGateway(onlineRechargeModel)
                emit(response)
            }


        }.flowOn(Dispatchers.IO)
    }

}