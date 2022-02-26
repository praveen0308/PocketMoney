package com.jmm.network.services

import com.google.gson.JsonObject
import com.jmm.model.DthCustomerDetail
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.RechargeHistoryModel
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

    @POST("Service/CallGooglePlayRechargeServiceSampurnaAPI")
    suspend fun callPlayRechargeServiceSampurnaApi(
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


    @POST("Wallet/WalletChargeDeduction")
    suspend fun walletChargeDeduction(
        @Query("userid") userId: String,
        @Query("walletId") walletId: Int,
        @Query("amount") amount: Double,
        @Query("requestId") requestId: String,
        @Query("serviceId") serviceId: Int
    ): Int


    //# region New Recharge Service

    @POST("Service/OnlineMobileRechargeWithWallet")
    suspend fun onlineMobileRechargeWithWallet(
        @Body mobileRechargeModel: MobileRechargeModel
    ): MobileRechargeModel


    @POST("Service/OnlineMobileRechargeWithGateway")
    suspend fun onlineMobileRechargeWithGateway(
        @Body onlineRechargeModel: JsonObject,
    ): MobileRechargeModel


    @POST("Service/OnlineDTHRechargeWithWallet")
    suspend fun onlineDthWithWallet(
        @Body mobileRechargeModel: MobileRechargeModel
    ): MobileRechargeModel


    @POST("Service/OnlineDTHRechargeWithGateway")
    suspend fun onlineDthWithGateway(
        @Body onlineRechargeModel: JsonObject,
    ): MobileRechargeModel


    //#endregion

}
