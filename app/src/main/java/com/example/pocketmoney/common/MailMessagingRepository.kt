package com.example.pocketmoney.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MailMessagingRepository @Inject constructor(
    val mailMessagingService: MailMessagingService
){
    suspend fun sendWhatsappMessage(mobileNo: String,message:String): Flow<Boolean> {
        return flow {
            val response = mailMessagingService.sendWhatsappMessage(mobileNo, message)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}