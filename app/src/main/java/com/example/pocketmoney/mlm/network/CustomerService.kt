package com.example.pocketmoney.mlm.network

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

}