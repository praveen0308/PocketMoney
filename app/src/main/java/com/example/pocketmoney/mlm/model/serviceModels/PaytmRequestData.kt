package com.example.pocketmoney.mlm.model.serviceModels

data class PaytmRequestData(
    val account: String? = null,
    val amount: String? = null,
    val callbackurl: String? = null,
    val email: String? = null,
    val ifsc: String? = null,
    val mobileNumber: String? = null,
    val orderid: String? = null,
    val transfermode: String? = null,
    val userid: String? = null
)