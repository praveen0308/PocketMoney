package com.jmm.model.serviceModels

data class MobileRechargeModel(
    var ErrorCode: Int? = null,
    var Message: String? = null,
    var MobileNo: String? = null,
    var OperatorCode: String? = null,
    var OperatorTransID: String? = null,
    var RechargeAmt: Double? = null,
    var Remark: String? = null,
    var RequestID: String? = null,
    var ServiceField1: String? = null,
    var ServiceField2: String? = null,
    var ServiceProviderID: Int? = null,
    var ServiceTypeID: Int? = null,
    var Status: String? = null,
    var TransTypeID: Int? = null,
    var UserID: String? = null,
    var WalletTypeID: Int? = null
)