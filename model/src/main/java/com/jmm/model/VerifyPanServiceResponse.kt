package com.jmm.model

import com.google.gson.annotations.SerializedName


data class VerifyPanServiceResponse(
    @SerializedName("data")
    val panData: PanData? = null,
    val message: Any? = null,
    val message_code: String? = null,
    val status_code: Int? = null,
    val success: Boolean? = null
)