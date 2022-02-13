package com.jmm.model.shopping_models.orderModule

data class OrderListModel(
    val Discount: Double,
    val GrandTotal: Double,
    val OrderDate: String,
    val OrderNumber: String,
    val OrderStatus: Int,
    val OrderStatusId: Int,
    val PaymentStatus: Int,
    val PaymentStatusId: Int,
    val Shipping: Double,
    val ShippingStatus: Int,
    val ShippingStatusId: Int,
    val SubTotal: Any,
    val Tax: Double,
    val Total: Double
)