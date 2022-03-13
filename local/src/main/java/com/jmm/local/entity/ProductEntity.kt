package com.jmm.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Products")
data class ProductEntity(
    @PrimaryKey
    val ProductId: Int,
    val CategoryId: Int,
    val CategoryName: String,
    val Description: String,
    val FeaturedProductInd: Boolean,
    val ItemId: Int,
    val MainPageInd: Boolean,
    val OldPrice: Double,
    val Price: Double,

    val ProductName: String,
    val ImagePath: String,
    val Saving: Double,
    val SpecialOfferInd: Boolean,
    val StockQuantity: Int
)
