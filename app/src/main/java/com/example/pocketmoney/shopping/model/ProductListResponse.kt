package com.example.pocketmoney.shopping.model

import com.example.pocketmoney.utils.myEnums.MyEnums

data class ProductListResponse(
    val ProductModel: List<ProductModel>,
    val ViewType : MyEnums,
    val Title : String

)