package com.example.pocketmoney.shopping.model


data class ProductModel(

    val CategoryId: Int,
    val CategoryName: String,
    val Description: Any,
    val FeaturedProductInd: Boolean,
    val ItemId: Int,
    val MainPageInd: Boolean,
    val OldPrice: Double,
    val Price: Double,
    val ProductId: Int,
    val ProductName: String,
    val Product_Image: List<ProductImage>,
    val Saving: Double,
    val SpecialOfferInd: Boolean,
    val StockQuantity: Int
)