package com.jmm.network.interceptors

import com.jmm.network.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RefreshAuthInterceptor @Inject constructor(preferencesManager: PreferencesManager) : Interceptor {
    private val refreshToken = preferencesManager.getRefreshToken()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $refreshToken")
            .build()
        return chain.proceed(request)
    }
}