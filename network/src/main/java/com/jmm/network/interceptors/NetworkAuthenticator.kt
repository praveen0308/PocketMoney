package com.jmm.network.interceptors

import com.jmm.model.authmodels.TokenResponse
import com.jmm.network.PreferencesManager
import com.jmm.network.services.AuthService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkAuthenticator @Inject constructor(
    private val authService: AuthService,
    val preferencesManager: PreferencesManager
) : Authenticator {
    var accessToken: String? = null
    override fun authenticate(route: Route?, response: Response): Request? {
        if (!response.request.header("Authorization").equals(accessToken)) return null

        /***
         *  Requesting new access token
         *
         * **/
        Timber.d("Requesting new token")
        val refreshToken = preferencesManager.getRefreshToken()

        val call: Call<TokenResponse> = authService.refreshToken(refreshToken!!, "refresh_token")
        call.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(
                call: Call<TokenResponse>,
                response: retrofit2.Response<TokenResponse>
            ) {
                val tokenResponse: TokenResponse? = response.body()
                tokenResponse?.let {
                    it.accessToken?.let { data ->
                        accessToken = data
                        preferencesManager.updateAccessToken(accessToken!!)
                    }
                    it.refreshToken?.let { data ->
                        preferencesManager.updateRefreshToken(data)
                    }
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Timber.d("Failed while requesting new token !!!")


            }
        })



        return if (accessToken != null) {
            response.request
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else null


    }

}