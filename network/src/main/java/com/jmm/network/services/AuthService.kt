package com.jmm.network.services

import com.jmm.model.authmodels.TokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    /***
     * This is to refresh access token using stored refresh token
     *
     * **/

    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String,
    ): TokenResponse?

    @FormUrlEncoded
    @POST("token")
    fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String
    ): Call<TokenResponse>
}