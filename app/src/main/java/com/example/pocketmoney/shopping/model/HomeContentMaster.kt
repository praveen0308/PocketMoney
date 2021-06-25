package com.example.pocketmoney.shopping.model

import com.example.pocketmoney.utils.myEnums.MyEnums

data class HomeContentMaster(
    val ProductModel: List<*>? = null,
    val ViewType: MyEnums,
    val Title: String
)