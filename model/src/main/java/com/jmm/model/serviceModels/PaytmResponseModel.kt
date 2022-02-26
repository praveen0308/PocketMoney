package com.jmm.model.serviceModels

data class PaytmResponseModel(
    var BANKTXNID: String? = null,
    var CHARGEAMOUNT: String? = null,
    var CURRENCY: String? = null,
    var GATEWAYNAME: String? = null,
    var MID: String? = null,
    var ORDERID: String? = null,
    var PAYMENTMODE: String? = null,
    var RESPCODE: String? = null,
    var RESPMSG: String? = null,
    var STATUS: String? = null,
    var TXNAMOUNT: String? = null,
    var TXNDATE: String? = null,
    var TXNID: String? = null
)