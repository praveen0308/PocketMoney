package com.example.pocketmoney.shopping.model.orderModule

data class OrderItem(
    val Comment: Any,
    val CreatedOn: String,
    val Discount: Double,
    val ID: Int,
    val ItemID: Int,
    val OrderItemStatusId: Int,
    val OrderNumber: String,
    val ProductPrice: Double,
    val Quantity: Int,
    val ShippingItemStatusId: Int,
    val UpdatedOn: Any
)