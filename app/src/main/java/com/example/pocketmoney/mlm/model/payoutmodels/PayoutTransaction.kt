package com.example.pocketmoney.mlm.model.payoutmodels

data class PayoutTransaction(
    val Account: String? = null,
    val AssociatedUser: String? = null,
    val BeneficiaryID: Int? = null,
    val BeneficiaryName: String? = null,
    val BeneficiaryType: Int? = null,
    val Comment: String? = null,
    val CustomerID: String? = null,
    val IFSCCode: String? = null,
    val IsActive: Boolean? = null,
    val ReferenceID: String? = null,
    val Status: String? = null,
    val TransactionType: Int? = null,
    val TransferAmount: Double? = null,
    val TransferDate: String? = null,
    val UserID: String? = null
)