package com.jmm.model.shopping_models

data class ProductCategory(
    val Description: String,
    val ID: Int,
    val IsActive: Boolean,
    val MainCategoryId: Int,
    val Name: String
)