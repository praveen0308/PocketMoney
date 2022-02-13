package com.jmm.model.shopping_models

import com.jmm.model.myEnums.PaymentEnum

data class ModelPaymentMethod(
    val method: PaymentEnum,
    val methodName:String,
    val imageUrl:Int,
    var isSelected:Boolean?=false,
)
