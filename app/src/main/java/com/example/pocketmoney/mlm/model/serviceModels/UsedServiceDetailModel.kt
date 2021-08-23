package com.example.pocketmoney.mlm.model.serviceModels

data class UsedServiceDetailModel(
    val ErrorCode: Int? = null,
    val Message: String? = null,
    val MobileNo: String? = null,
    val OperatorCode: String? = null,
    val OperatorTransID: String? = null,
    val RechargeAmt: Double? = null,
    val Remark: String? = null,
    val RequestID: String? = null,
    val ServiceField1: String? = null,
    val ServiceField2: String? = null,
    val ServiceProviderID: Int? = null,
    val ServiceTypeID: Int? = null,
    val Status: String? = null,
    val TransTypeID: Int? = null,
    val UserID: String? = null,
    val WalletTypeID: Int? = null
)