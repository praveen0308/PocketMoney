package com.jmm.repository

import com.jmm.model.SMSResponseModel
import com.jmm.network.services.MailMessagingService
import com.jmm.network.services.SMSService
import com.jmm.network.services.SmsServiceConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MailMessagingRepository @Inject constructor(
    val mailMessagingService: MailMessagingService,
    val smsService: SMSService
) {
    suspend fun sendWhatsappMessage(mobileNo: String, message: String): Flow<Boolean> {
        return flow {
            val response = mailMessagingService.sendWhatsappMessage(mobileNo, message)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendRegistrationMessage(
        mobileNo: String,
        userId: String,
        password: String
    ): Flow<SMSResponseModel> {
        val msg =
            "Welcome! You are registered with pocketmoney USERID - $userId, PASSWORD - $password and Login to pocketmoney.net.in"
        return flow {
            val response = smsService.sendSMS(
                SmsServiceConstants.USER_NAME,
                SmsServiceConstants.API_KEY,
                SmsServiceConstants.API_REQUEST,
                SmsServiceConstants.SENDER_ID,
                mobileNo,
                msg,
                SmsServiceConstants.ROUTE,
                SmsServiceConstants.REG_TEMPLATE_ID,
                SmsServiceConstants.FORMAT

            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendOtpSMS(mobileNo: String, otp: String): Flow<SMSResponseModel> {
        val msg =
            "Your pocketmoney One Time Password(OTP) is $otp. Do not share this OTP to anyone for security reasons."
        return flow {
            val response = smsService.sendSMS(
                SmsServiceConstants.USER_NAME,
                SmsServiceConstants.API_KEY,
                SmsServiceConstants.API_REQUEST,
                SmsServiceConstants.SENDER_ID,
                mobileNo,
                msg,
                SmsServiceConstants.ROUTE,
                SmsServiceConstants.OTP_TEMPLATE_ID,
                SmsServiceConstants.FORMAT

            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun sendPayoutSMS(
        mobileNo: String,
        senderName: String,
        amount: String,
        accountNumber: String,
        beneficiary: String,
        mode: String,
    ): Flow<SMSResponseModel> {
//        val msg = "Dear $senderName Rs $amount transferred successfully beneficiary $accountNumber and beneficiary Name $beneficiary Pocket money"
        val msg =
            "Dear $senderName, Rs.$amount is transferred successfully to beneficiary $beneficiary $accountNumber via $mode .Thank you,Pocket Money"
        return flow {
            val response = smsService.sendSMS(

                SmsServiceConstants.USER_NAME,
                SmsServiceConstants.API_KEY,
                SmsServiceConstants.API_REQUEST,
                SmsServiceConstants.SENDER_ID,
                mobileNo,
                msg,
                SmsServiceConstants.ROUTE,
                SmsServiceConstants.PAYOUT_TEMPLATE_ID,
                SmsServiceConstants.FORMAT

            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}