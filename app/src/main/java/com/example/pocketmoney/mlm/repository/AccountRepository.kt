package com.example.pocketmoney.mlm.repository

import android.content.Context
import android.provider.ContactsContract
import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.model.MyPreferenceKeys
import com.example.pocketmoney.mlm.model.UserMenu
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.network.MLMApiService
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.Constants.PREFERENCE_NAME
import com.example.pocketmoney.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

class AccountRepository @Inject constructor(
        private val mlmApiService: MLMApiService,
        context: Context
) {
    private val dataStore: DataStore<Preferences> = context.createDataStore(
            name = PREFERENCE_NAME
    )

    val userID: Flow<String> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                val userId = preference[MyPreferenceKeys.userId]
                userId!!
            }

    val roleID: Flow<Int> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                val roleId = preference[MyPreferenceKeys.userRoleId]
                roleId!!
            }

    val welcomeState: Flow<Int> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                val welcomeStatus = preference[MyPreferenceKeys.welcomeStatus] ?: Constants.NEW_USER
                welcomeStatus
            }


    suspend fun updateWelcomeStatus(status: Int) {
        dataStore.edit { preference ->
            preference[MyPreferenceKeys.welcomeStatus] = status
        }
    }

    suspend fun storeUserLoginInfo(userModel: UserModel) {

        dataStore.edit { preference ->
            preference[MyPreferenceKeys.loginId] = userModel.LoginID!!
            preference[MyPreferenceKeys.userId] = userModel.UserID!!
            preference[MyPreferenceKeys.userName] = userModel.UserName!!
            preference[MyPreferenceKeys.userRoleId] = userModel.UserRoleID!!

        }
    }

    suspend fun doLogin(userName: String, password: String): Flow<DataState<UserModel>> = flow {
        emit(DataState.Loading)

        try {

            val userModel = mlmApiService.doLogin(userName, password)
            if (userModel != null) {
                storeUserLoginInfo(userModel)
                emit(DataState.Success(userModel))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
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