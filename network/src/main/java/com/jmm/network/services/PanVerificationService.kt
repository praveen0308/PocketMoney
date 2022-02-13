package com.jmm.network.services

import com.google.gson.JsonObject
import com.jmm.model.VerifyPanServiceResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PanVerificationService {

    @POST("pan/pan")
    suspend fun verifyPanNumber(
        @Body jsonObject: JsonObject
    ): VerifyPanServiceResponse

}