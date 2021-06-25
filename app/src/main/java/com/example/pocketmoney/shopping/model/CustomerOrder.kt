package com.example.pocketmoney.shopping.model

data class CustomerOrder(
    val BillingAddressId: Int=0,
    val Discount: Double=0.0,
    val GrandTotal: Double=0.0,
    val ID: Int=0,
    val ItemDiscount: Double=0.0,
    val OrderDate: String?=null,
    val OrderNumber: String?=null,
    val OrderStatusId: Int=0,
    val Payment: Double=0.0,
    val PaymentMethod: String?=null,
    val PaymentStatusId: Int=0,
    val Promo: String?=null,
    val RedeemRewardPointEntryId: Int=0,
    val RefundedAmount: Double=0.0,
    val RewardPointHistoryId: Int=0,
    val SessionID: String?=null,
    val Shipping: Double=0.0,
    val ShippingAddressId: Int=0,
    val ShippingStatusId: Int=0,
    val SubTotal: Double=0.0,
    val Tax: Double=0.0,
    val Token: String?=null,
    val Total: Double=0.0,
    val TransactionNumber: String?=null,
    val UserID: String
)