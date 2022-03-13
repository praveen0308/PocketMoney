package com.jmm.repository

import com.jmm.network.PreferencesManager
import com.jmm.network.services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authService: AuthService
){

    suspend fun getAccessToken(userName:String,password:String): Flow<Boolean> {
        return flow {
            val response = authService.getAccessToken(userName,password,"password")
            if (response!=null){
                preferencesManager.updateAccessToken(response.accessToken!!)
                preferencesManager.updateRefreshToken(response.refreshToken!!)
                preferencesManager.updateTokenExpiry(response.expiryDate!!)
                kotlinx.coroutines.delay(2000)
                emit(true)
            }
            else{
                emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }
}