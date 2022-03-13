package com.jmm.model.authmodels

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName(".expires")
    val expiryDate: String? = null,
    @SerializedName(".issued")
    val dateOfIssue: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("expires_in")
    val expiryInSeconds: Int? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("roles")
    val roleId: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    val userName: String? = null
)