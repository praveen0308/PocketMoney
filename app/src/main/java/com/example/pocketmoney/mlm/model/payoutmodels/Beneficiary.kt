package com.example.pocketmoney.mlm.model.payoutmodels

data class Beneficiary(
    val Account: String? = null,
    val AssociatedUser: String? = null,
    val BeneficiaryID: Int? = null,
    val BeneficiaryName: String? = null,
    val CustomerID: Any? = null,
    val IFSCCode: String? = null,
    val IsActive: Boolean? = null,
    val Type: Int? = null
)