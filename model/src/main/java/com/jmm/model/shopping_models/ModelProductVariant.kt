package com.jmm.model.shopping_models

data class ModelProductVariant(
        var title:String?=null,
        var VariantID:Int?=null,
        var variantValueList:MutableList<ProductVariantValue>?=null
)
