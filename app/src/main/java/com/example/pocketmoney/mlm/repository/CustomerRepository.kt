package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.ComplainModel
import com.example.pocketmoney.mlm.model.CustomerDetailResponse
import com.example.pocketmoney.mlm.model.mlmModels.*
import com.example.pocketmoney.mlm.network.CustomerService
import com.example.pocketmoney.mlm.network.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CustomerRepository @Inject constructor(
    private val mlmApiService: MLMApiService,
    private val customerService: CustomerService
) {

    suspend fun getCustomerDetail(userID: String): Flow<CustomerDetailResponse> {
        return flow {
            val response = mlmApiService.getCustomerDetails(userID)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerGrowth(requestModel1: CustomerRequestModel1): Flow<CustomerGrowthResponse> {
        return flow {
            val response = mlmApiService.getCustomerGrowth(requestModel1)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserProfile(id: String): Flow<CustomerProfileModel> {

        return flow {
            val response = mlmApiService.getCustomerProfileInfo(id)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getGrowthCommission(requestModel: GrowthComissionRequestModel): Flow<GrowthCommissionResponse> {

        return flow {
            val response = mlmApiService.getGrowthCommission(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerDirectCommission(requestModel: GrowthComissionRequestModel): Flow<List<CommissionHistoryModel>> {
        return flow {
            val response = mlmApiService.getCustomerDirectCommission(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerUpdateCommission(requestModel: GrowthComissionRequestModel): Flow<List<CommissionHistoryModel>> {
        return flow {
            val response = mlmApiService.getCustomerUpdateCommission(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerServiceCommission(requestModel: GrowthComissionRequestModel): Flow<List<CommissionHistoryModel>> {
        return flow {
            val response = mlmApiService.getCustomerServiceCommission(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getCustomerShoppingCommission(requestModel: GrowthComissionRequestModel): Flow<List<CommissionHistoryModel>> {
        return flow {
            val response = mlmApiService.getCustomerShoppingCommission(requestModel)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getCouponList(userID:String,roleId:Int,fromDate:String,toDate:String): Flow<List<CouponModel>> {
        return flow {
            val response = mlmApiService.getCouponList(userID,roleId, fromDate, toDate)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getComplaintHistory(userID:String,roleId:Int,fromDate:String,toDate:String,filter:String,condition:String): Flow<List<CustomerComplaintModel>> {
        return flow {
            val response = mlmApiService.getComplaintHistory(userID,roleId, fromDate, toDate,filter,condition)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun validateCustomerRegistration(mobileNo:String,pin:String,pinSerialNo:String): Flow<Int> {
        return flow {
            val response = customerService.validateCustomerRegistration(mobileNo,pin, pinSerialNo)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun activateAccountUsingCoupon(userID: String,pin:String,pinSerialNo:String): Flow<Int> {
        return flow {
            val response = customerService.activateCustomerAccount(userID,pin, pinSerialNo)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun onlineActivateAccount(userID: String,walletTypeId:Int): Flow<Int> {
        return flow {
            val response = customerService.onlineActivateCustomerAccount(userID,walletTypeId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addServiceComplain(requestId: String,transactionId:String,userId:String,comment:String): Flow<String> {
        return flow {
            val response = customerService.addServiceComplaint(requestId, transactionId, userId, comment)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun actionOnComplain(complainModel: ComplainModel): Flow<Int> {
        return flow {
            val response = customerService.actionOnComplaint(complainModel)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getComplaintChat(transactionId: String): Flow<List<ComplainModel>> {
        return flow {
            val response = customerService.getComplaintChat(transactionId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun generateNewCoupon(userId: String,walletId:Int,count:Int): Flow<Int> {
        return flow {
            val response = customerService.generateNewCoupon(userId, walletId, count)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}