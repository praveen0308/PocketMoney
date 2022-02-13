package com.jmm.repository

import com.jmm.model.CustomerKYCModel
import com.jmm.network.services.KYCService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class KycRepository @Inject constructor(
    private val kycService: KYCService
){
    suspend fun addCustomerKycPanDetail(userId:String,panNumber:String,panName:String): Flow<Int> {
        return flow {
            val response = kycService.updateCustomerKycPan(userId, panNumber, panName)
            emit(response)
        }.flowOn(Dispatchers.IO)

    }

    suspend fun addCustomerDetailDocument(customerKYCModel: CustomerKYCModel): Flow<Int> {
        return flow {

            val response = kycService.addCustomerKycDocument(customerKYCModel)

            emit(response)
        }.flowOn(Dispatchers.IO)

    }
}