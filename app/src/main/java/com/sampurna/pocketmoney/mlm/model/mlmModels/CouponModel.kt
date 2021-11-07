package com.sampurna.pocketmoney.mlm.model.mlmModels

data class CouponModel(
    val Action: Any,
    val FromDate: Any,
    val FullName: String,
    val GeneratedBy: Int,
    val GeneratedOn: String,
    val IsCancel: Boolean,
    val LoginID: Int,
    val MemberID: Double,
    val PinNo: Int,
    val PinSerialNo: Int,
    val PinStatus: String,
    val PinTypeId: Int,
    val ToDate: Any,
    val UserID: Double,
    val UserRoleID: Int
)