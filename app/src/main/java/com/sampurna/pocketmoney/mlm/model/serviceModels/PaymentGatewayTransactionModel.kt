package com.sampurna.pocketmoney.mlm.model.serviceModels

data class PaymentGatewayTransactionModel(
    val BankName: String? = null,
    val BankTxnId: String? = null,
    val Currency: String? = null,
    val FullName: String? = null,
    val GateWayName: String? = null,
    val Id: Int? = null,
    val IsActive: Boolean? = null,
    val IsCredit: Boolean? = null,
    val OrderId: String? = null,
    val PaymentId: String? = null,
    val PaymentMode: String? = null,
    val PaymentModeId: Int? = null,
    val ReferenceTransactionId: String? = null,
    val RespCode: String? = null,
    val RespMsg: String? = null,
    val Service: String? = null,
    val ServiceTypeId: Int? = null,
    val Status: String? = null,
    val TransactionDate: String? = null,
    val TransactionTypeId: Int? = null,
    val TxnAmount: String? = null,
    val TxnId: String? = null,
    val UserId: String? = null,
    val WalletTypeId: Int? = null
)