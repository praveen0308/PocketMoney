package com.jmm.repository



import com.jmm.model.ModelCustomerDetail
import com.jmm.model.UserMenu
import com.jmm.model.UserModel
import com.jmm.network.services.CustomerService
import com.jmm.network.services.MLMApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val mlmApiService: MLMApiService,
    private val customerService: CustomerService,
    private val userAccountData: UserAccountData,
    userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun doLogin(userId:String,password:String): Flow<UserModel?> {
        return flow {
            val response = mlmApiService.doLogin(userId, password)

            emit(response.body())
        }.flowOn(Dispatchers.IO)

    }

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
    ): Flow<ModelCustomerDetail> {
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

  /*  suspend fun getDashboardData(
        userId: String,
        roleId: Int
    ): Flow<JsonObject> {
        return flow {
            val response = mlmApiService.getDashboardData(userId,roleId)
//            emit(response)
        }.flowOn(Dispatchers.IO)
    }*/
    suspend fun getDashboardData(userId: String,roleId:Int) = networkBoundResource(
        query = {
            flowOf(userAccountData.getCustomerDashboardData())
        },
        fetch = {
            mlmApiService.getDashboardData(userId, roleId)
        },
        saveFetchResult = { data ->
            userAccountData.updateCustomerAccountData(data)
            userAccountData.getCustomerDashboardData()
        }
    )
    suspend fun resetPassword(
        userId: String,
        loginId: Int,
        otp: String,
        action: String
    ): Flow<Boolean> {
        return flow {
            val response = customerService.resetPassword(userId,loginId, otp, action)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}