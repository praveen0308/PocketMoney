package com.sampurna.pocketmoney.shopping.model

data class ModelProductVariant(
        var title:String?=null,
        var VariantID:Int?=null,
        var variantValueList:MutableList<ProductVariantValue>?=null
)
