package com.sampurna.pocketmoney.mlm.model.mlmModels

data class CustomerRequestModel1(
    val Filter: String?=null,
    val FromDate: String?=null,
    val MemberID: Double=0.0,
    val MemberRoleID: Int=0,
    val MemberStatus: Int=0,
    val ReferenceID: String?=null,
    val RoleID: Int,
    val ToDate: String?=null,
    val UserID: Long
)