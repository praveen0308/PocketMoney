package com.jmm.network.services

import com.jmm.model.SMSResponseModel
import retrofit2.http.POST
import retrofit2.http.Query


interface SMSService {
    @POST("http/index.php")
    suspend fun sendSMS(
        @Query("username") userName: String,
        @Query("apikey") apiKey: String,
        @Query("apirequest") apiRequest: String,
        @Query("sender") sender: String,
        @Query("mobile") mobileNo: String,
        @Query("message") msg: String,
        @Query("route") route: String,
        @Query("TemplateID") templateId: String,
        @Query("format") format: String,
    ): SMSResponseModel
}