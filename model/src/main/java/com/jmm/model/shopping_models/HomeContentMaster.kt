package com.jmm.model.shopping_models

import com.jmm.model.myEnums.MyEnums

data class HomeContentMaster(
    val ProductModel: List<*>? = null,
    val ViewType: MyEnums,
    val Title: String
)