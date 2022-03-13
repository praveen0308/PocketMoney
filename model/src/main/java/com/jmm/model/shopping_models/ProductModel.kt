package com.jmm.model.shopping_models


data class ProductModel(

    val CategoryId: Int,
    val CategoryName: String,
    val Description: String?,
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