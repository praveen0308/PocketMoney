package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.DthCustomerDetail
import com.example.pocketmoney.mlm.model.serviceModels.MobileRechargeModel
import com.example.pocketmoney.mlm.model.serviceModels.RechargeHistoryModel
import com.example.pocketmoney.mlm.model.serviceModels.UsedServiceDetailModel
import com.example.pocketmoney.mlm.ui.dth.DthOperator
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RechargeAPIService {
    @POST("Service/AddServiceUsedDetails")
    suspend fun addUsedServiceDetail(
       @Body usedServiceDetailModel: MobileRechargeModel
    ): Int

    @POST("Service/GetServiceUsedHistory")
    suspend fun getUsedServiceHistory(
        @Body jsonObject: JsonObject
    ): List<RechargeHistoryModel>


    @POST("Service/GetServiceUsedReqID")
    suspend fun getServiceUsedRequestId(
        @Query("userId") userId: String,
        @Query("mobileno") mobileNumber: String
    ): String


    @POST("Service/CallMobileRechargeServiceSampurnaAPI")
    suspend fun callMobileRechargeServiceSampurnaApi(
        @Body mobileRechargeModel: MobileRechargeModel
    ): MobileRechargeModel

    @POST("Service/CallDTHRechargeServiceSampurnaAPI")
    suspend fun callDTHRechargeServiceSampurnaApi(
        @Body mobileRechargeModel: MobileRechargeModel
    ): MobileRechargeModel

    @GET("Recharge/GetServiceOperator")
    suspend fun getServiceOperator(
        @Query("ServiceTypeId") serviceTypeId: Int,
        @Query("ServiceProviderId") serviceProviderId: Int,
        @Query("CircleCode") circleCode: Int
    ): Int

    @GET("Recharge/GetDTHCustomerDetails")
    suspend fun getDthCustomerDetails(
        @Query("account") account: String,
        @Query("opcode") operatorCode: String
    ): DthCustomerDetail


    @GET("Wallet/WalletChargeDeduction")
    suspend fun walletChargeDeduction(
        @Query("userid") userId: String,
        @Query("walletId") walletId: Int,
        @Query("amount") amount: Double,
        @Query("requestId") requestId: String,
        @Query("serviceId") serviceId: Int
    ): Int


}
