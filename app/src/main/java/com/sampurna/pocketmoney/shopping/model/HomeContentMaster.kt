package com.sampurna.pocketmoney.shopping.model

import com.sampurna.pocketmoney.utils.myEnums.MyEnums

data class HomeContentMaster(
    val ProductModel: List<*>? = null,
    val ViewType: MyEnums,
    val Title: String
)