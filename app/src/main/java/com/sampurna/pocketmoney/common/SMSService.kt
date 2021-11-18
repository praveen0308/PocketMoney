package com.sampurna.pocketmoney.common

import com.google.gson.JsonObject
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface SMSService {
    @POST("?")
    suspend fun sendSMS(
        @Url url: String,
        @Query("username") userName: String,
        @Query("apikey") apiKey: String,
        @Query("apirequest") apiRequest: String,
        @Query("sender") sender: String,
        @Query("mobile") mobileNo: String,
        @Query("message") msg: String,
        @Query("route") route: String,
        @Query("TemplateID") templateId: String,
        @Query("format") format: String,
    ): JsonObject
}