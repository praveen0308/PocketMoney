package com.jmm.network.services

import com.jmm.model.CustomerKYCModel
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface KYCService {

    @POST("Customer/UpdateCustomerKYCDetails")
    suspend fun updateCustomerKycPan(
        @Query("UserID") userId:String,
        @Query("PanNumber") panNumber:String,
        @Query("PanName") panName:String,
    ): Int

    @POST("Customer/AddCustomerKYCDocument")
    suspend fun addCustomerKycDocument(
        @Body customerKYCModel: CustomerKYCModel
    ): Int
}