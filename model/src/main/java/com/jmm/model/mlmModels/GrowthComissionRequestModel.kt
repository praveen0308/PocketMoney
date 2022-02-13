package com.jmm.model.mlmModels

data class GrowthComissionRequestModel(
    val UserID: String,
    val RoleID: Int,
    val FromDate: String?=null,
    val ToDate: String?=null,
    val Action: String="",
    val Filter: String=""

)