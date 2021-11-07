package com.sampurna.pocketmoney.shopping.model

import com.sampurna.pocketmoney.utils.myEnums.MyEnums

data class ProductListResponse(
    val ProductModel: List<ProductModel>,
    val ViewType : MyEnums,
    val Title : String

)