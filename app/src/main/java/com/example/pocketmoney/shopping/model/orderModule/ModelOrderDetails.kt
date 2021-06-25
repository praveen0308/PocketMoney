package com.example.pocketmoney.shopping.model.orderModule

data class ModelOrderDetails(
    val CustomerAddress: CustomerAddress,
    val OrderItem: List<OrderItem>,
    val OrderItemListModel: List<OrderItemModel>,
    val OrderListModel: OrderListModel,
    val OrderNoteList: List<OrderNote>,
    val Orders: Any,
    val Product_Image: Any,
    val Product_Item: Any,
    val ShippingDetail: List<Any>,
    val ShippingDetailAddress: ShippingDetailAddress,
    val ShippingItem: List<Any>
)