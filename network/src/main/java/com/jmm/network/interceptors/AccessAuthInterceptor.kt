package com.jmm.network.interceptors

import com.jmm.network.PreferencesManager
import com.jmm.network.services.AuthService
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AccessAuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authService: AuthService
) : Interceptor {
    private var accessToken = preferencesManager.getAccessToken()
    private val refreshToken = preferencesManager.getRefreshToken()

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this){
            if (preferencesManager.isTokenExpired()) {
                Timber.e("Token expired!!!")

                Timber.d("Going to refresh token!!!")

                val newTokenResponse =
                    authService.refreshToken(refreshToken!!, "refresh_token").execute()

                if (newTokenResponse.isSuccessful){
                    Timber.d("Received refresh token response...")
                    val tokenResponse = newTokenResponse.body()
                    Timber.d("Refresh token response : $tokenResponse")
                    if (tokenResponse != null) {
                        tokenResponse.accessToken?.let { data ->
                            accessToken = data
                            preferencesManager.updateAccessToken(accessToken!!)
                        }
                        tokenResponse.refreshToken?.let { data ->
                            preferencesManager.updateRefreshToken(data)
                        }

                    }
                }else{
                    Timber.e("Failed while requesting new token !!!")
                }

                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
                return chain.proceed(newRequest)

            } else {
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
                return chain.proceed(request)
            }

        }


    }

}