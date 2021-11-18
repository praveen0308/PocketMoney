package com.sampurna.pocketmoney.common

import retrofit2.http.POST
import retrofit2.http.Query

interface MailMessagingService {

    @POST("Mail/SendWhattsupMessage")
    suspend fun sendWhatsappMessage(
        @Query("mobileNumber") mobileNo: String,
        @Query("message") message: String
    ): Boolean

    @POST("Mail/SendEmail")
    suspend fun sendEmail(
        @Query("recipientEmail") recipientEmail: String,
        @Query("subject") subject: String
    ): Boolean

}