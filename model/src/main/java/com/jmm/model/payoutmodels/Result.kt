package com.jmm.model.payoutmodels

data class Result(
    val amount: Any? = null,
    val beneficiaryIfsc: Any? = null,
    val beneficiaryName: Any? = null,
    val cachedTime: Any? = null,
    val commissionAmount: Any? = null,
    val isCachedData: Any? = null,
    val mid: Any? = null,
    val orderId: Any? = null,
    val paytmOrderId: String? = null,
    val reversalReason: Any? = null,
    val rrn: Any? = null,
    val tax: Any? = null
)