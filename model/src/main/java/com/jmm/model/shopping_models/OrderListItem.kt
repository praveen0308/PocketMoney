package com.jmm.model.shopping_models

data class OrderListItem(
    val Discount: Any,
    val GrandTotal: Double,
    val OrderDate: String,
    val OrderNumber: String,
    val OrderStatus: Int,
    val OrderStatusId: Int,
    val PaymentStatus: Int,
    val PaymentStatusId: Int,
    val Shipping: Any,
    val ShippingStatus: Int,
    val ShippingStatusId: Int,
    val SubTotal: Any,
    val Tax: Any,
    val Total: Double
)