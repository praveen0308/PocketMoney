package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.ComplainModel
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomerService {
    @POST("Customer/ValidateCustomerRegistration")
    suspend fun validateCustomerRegistration(
        @Query("mobile") mobile: String,
        @Query("pin") pin: String,
        @Query("pinserial") pinSerialNo: String
    ): Int

    @POST("Customer/ActivateCustomerAccount")
    suspend fun activateCustomerAccount(
        @Query("userid") userId: String,
        @Query("pin") pin: String,
        @Query("pinserial") pinSerialNo: String
    ): Int

    @POST("Customer/OnlineActivateCustomerAccount")
    suspend fun onlineActivateCustomerAccount(
        @Query("userid") userId: String,
        @Query("walletTypeid") walletTypeId: Int
    ): Int

    @POST("Customer/AddServiceComplains")
    suspend fun addServiceComplaint(
        @Query("requestId") requestId: String,
        @Query("transId") transactionId: String,
        @Query("userid") userId: String,
        @Query("comment") comment:String
    ): String

    @POST("Customer/GetComplainChat")
    suspend fun getComplaintChat(
        @Query("id") id: String,
    ): List<ComplainModel>

    @POST("Customer/GenerateNewPin")
    suspend fun generateNewCoupon(
        @Query("userId") userId: String,
        @Query("walletId") walletId: Int,
        @Query("count") count: Int
    ): Int
}
