package com.jmm.repository

import com.google.gson.JsonObject
import com.jmm.model.VerifyPanServiceResponse
import com.jmm.network.services.PanVerificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PanVerificationRepository @Inject constructor(
    private val panVerificationService: PanVerificationService
) {

    suspend fun verifyPanNumber(panNumber:String): Flow<VerifyPanServiceResponse> {
        return flow {
            val jsonObject = JsonObject()
            jsonObject.addProperty("id_number",panNumber)
            val response = panVerificationService.verifyPanNumber(jsonObject)

            emit(response)
        }.flowOn(Dispatchers.IO)

    }

}