package com.jmm.model.mlmModels

data class CustomerAuthBalanceResponse(
    val BusinessWallet: Double? = null,
    val IncomeWallet: Double? = null,
    val IsBlocked: Int? = null,
    val IsKYCRequired: Int? = null,
    val UserType: Int = 0,
)