package com.example.pocketmoney.mlm.model

data class TransactionModel(
    val Balance: Double,
    val Credit: Double,
    val Debit: Double,
    val Filter: Any,
    val FilterType: Any,
    val FromDate: Any,
    val MemberID: Double,
    val Reference_Id: String,
    val RequestStatus: Any,
    val RoleID: Int,
    val ToDate: Any,
    val Trans_Category: String,
    val Trans_Date: String,
    val Trans_Id: String,
    val UserID: Double
)