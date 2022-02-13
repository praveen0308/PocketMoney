package com.jmm.model.shopping_models

import com.jmm.model.myEnums.MyEnums


data class ProductListResponse(
    val ProductModel: List<ProductModel>,
    val ViewType : MyEnums,
    val Title : String

)