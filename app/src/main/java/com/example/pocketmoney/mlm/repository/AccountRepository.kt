package com.example.pocketmoney.mlm.repository

import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.network.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepository @Inject constructor(
        private val mlmApiService: MLMApiService
) {

    suspend fun doLogin(userId:String,password:String): Flow<UserModel> {
        return flow {
            val response = mlmApiService.doLogin(userId, password)

            emit(response)
        }.flowOn(Dispatchers.IO)

    }
//
//    suspend fun doLogin(userName: String, password: String): Flow<DataState<UserModel>> = flow {
//        emit(DataState.Loading)
//
//        try {
//
//            val userModel = mlmApiService.doLogin(userName, password)
//            if (userModel != null) {
//
//                emit(DataState.Success(userModel))
//            }
//
//        } catch (e: Exception) {
//            emit(DataState.Error(e))
//        }
//    }

    suspend fun checkAccountAlreadyExist(
            userId: String
    ): Flow<Boolean> {
        return flow {
            val response = mlmApiService.validateDuplicateAccount("Customer","UserID",userId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerUser(
            customerDetail: ModelCustomerDetail
    ): Flow<Boolean> {
        return flow {
            val response = mlmApiService.addCustomerDetails(customerDetail)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getSponsorName(
            id: String
    ): Flow<String> {
        return flow {
            val response = mlmApiService.getSponsorName(id)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserName(
            id: String
    ): Flow<String> {
        return flow {
            val response = mlmApiService.getUserName(id)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun isUserAccountActive(
            id: String
    ): Flow<Boolean> {
        return flow {
            val response = mlmApiService.getUserAccountStatus(id)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getUserMenus(
            userId: String
    ): Flow<List<UserMenu>> {
        return flow {
            val response = mlmApiService.getUserMenus(userId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


}