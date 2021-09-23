package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.UsedServiceDetailModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RechargeAPIService {
    @POST("Service/AddServiceUsedDetails")
    suspend fun addUsedServiceDetail(
       @Body usedServiceDetailModel: MobileRechargeModel
    ): Int

    @POST("Service/GetServiceUsedReqID")
    suspend fun getServiceUsedRequestId(
        @Query("userId") userId: String,
        @Query("mobileno") mobileNumber: String
    ): String

    @POST("Service/CallMobileRechargeServiceSampurnaAPI")
    suspend fun callMobileRechargeServiceSampurnaApi(
        @Body mobileRechargeModel: MobileRechargeModel
    ): MobileRechargeModel

    @GET("Recharge/GetServiceOperator")
    suspend fun getServiceOperator(
        @Query("ServiceTypeId") serviceTypeId: Int,
        @Query("ServiceProviderId") serviceProviderId: Int,
        @Query("CircleCode") circleCode: Int
    ): Int


    @GET("Wallet/WalletChargeDeduction")
    suspend fun walletChargeDeduction(
        @Query("userid") userId: String,
        @Query("walletId") walletId: Int,
        @Query("amount") amount: Double,
        @Query("requestId") requestId: String,
        @Query("serviceId") serviceId: Int
    ): Int


}