package com.sampurna.pocketmoney.shopping.model

data class CartModel(
    val CartId: Int,
    val CartStatusId: Any,
    val CartTrackId: Any,
    val Image_Id: Int,
    val Image_Path: Any,
    val Image_Url: Any,
    val Item_Id: Int,
    val MemberId: Any,
    val Old_Price: Double,
    val Price: Double,
    val ProductId: Any,
    val ProductImage: ProductImage,
    val ProductItem: Any,
    val ProductName: String,
    val Product_Id: Any,
    val Quantity: Int,
    val ShippingDetailId: Any,
    val Sold_Quantity: Any,
    val Stock_Quantity: Any
)