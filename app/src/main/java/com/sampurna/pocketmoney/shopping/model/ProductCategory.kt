package com.sampurna.pocketmoney.shopping.model

data class ProductCategory(
    val Description: String,
    val ID: Int,
    val IsActive: Boolean,
    val MainCategoryId: Int,
    val Name: String
)