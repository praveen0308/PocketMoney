package com.jmm.model

data class TransactionDetailModel(
    val Amount: Double? = null,
    val Balance: Double? = null,
    val ComplainID: String? = null,
    val Credit: Double? = null,
    val Debit: Double? = null,
    val MemberID: Double? = null,
    val Mobile_Account_No: String? = null,
    val Operator_Trans_ID: String? = null,
    val PaymentMode: Int? = null,
    val Reference_Id: String? = null,
    val RequestStatus: Any? = null,
    val RoleID: Int? = null,
    val Service_Status: String? = null,
    val Trans_Date: String? = null,
    val Trans_Id: String? = null,
    val Trans_Type: String? = null,
    val Trans_Type_Id: Int? = null,
    val Transfer_User: Any? = null,
    val UserID: String? = null,
    val Wallet_Id: Int? = null
)