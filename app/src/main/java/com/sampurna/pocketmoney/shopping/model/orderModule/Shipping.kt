package com.sampurna.pocketmoney.shopping.model.orderModule

data class Shipping(
    val AdminComment: String? = null,
    val CourierContactNumber: String? = null,
    val CourierName: String? = null,
    val CreateDate: Any? = null,
    val DeliveredBy: Any? = null,
    val DeliveredOn: String? = null,
    val ID: Int? = null,
    val IsActive: Any? = null,
    val OrderNumber: String? = null,
    val ShipmentCarrierId: Any? = null,
    val ShipmentSize: String? = null,
    val ShipmentWeight: String? = null,
    val ShippedOn: String? = null,
    val ShippingStatusId: Int? = null,
    val TrackingNumber: String? = null,
    val WareHouseId: Any? = null
)