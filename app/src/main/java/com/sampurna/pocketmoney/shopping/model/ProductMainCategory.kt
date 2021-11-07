package com.sampurna.pocketmoney.shopping.model

data class ProductMainCategory(
        val Description: String,
        val ID: Int,
        val IsActive: Boolean,
        val Name: String,
        var isSelected:Boolean=false,
        var type:Int=1
)