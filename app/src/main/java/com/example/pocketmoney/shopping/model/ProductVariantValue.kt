package com.example.pocketmoney.shopping.model

data class ProductVariantValue(
    val ID: Int,
    val Item_Id: Int,
    val Product_Id: Int,
    val Varients_Code: String,
    val Varients_Id: Int,
    val Varients_Value_Code: String,
    val Varients_Value_Id: Int,
    var isSelected:Boolean?=false
)