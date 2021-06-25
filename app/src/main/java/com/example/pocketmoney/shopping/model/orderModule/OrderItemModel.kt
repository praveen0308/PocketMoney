package com.example.pocketmoney.shopping.model.orderModule

data class OrderItemModel(
    val CreateDate: Any,
    val Discount: Double,
    val ID: Any,
    val ImageUrl: String,
    val ItemID: Int,
    val ItemName: Any,
    val OrderItemStatusId: Int,
    val OrderNumber: Any,
    val OrderStatus: Int,
    val ProductName: String,
    val ProductPrice: Double,
    val Quantity: Int,
    val SKU: String,
    val ShippingItemStatusId: Int,
    val ShippingStatus: Int
)