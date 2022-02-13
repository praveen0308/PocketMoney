package com.jmm.model

import com.google.gson.annotations.SerializedName

data class SMSResponseModel(
    val message: String? = null,
    @SerializedName("message-id")
    val message_id: List<String>? = null,
    val status: String? = null
)