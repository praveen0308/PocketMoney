package com.jmm.model.serviceModels

data class PaytmResponseModel(
    val BANKTXNID: String? = null,
    var CHARGEAMOUNT: String? = null,
    val CURRENCY: String? = null,
    val GATEWAYNAME: String? = null,
    val MID: String? = null,
    var ORDERID: String? = null,
    val PAYMENTMODE: String? = null,
    val RESPCODE: String? = null,
    val RESPMSG: String? = null,
    val STATUS: String? = null,
    val TXNAMOUNT: String? = null,
    val TXNDATE: String? = null,
    val TXNID: String? = null
)