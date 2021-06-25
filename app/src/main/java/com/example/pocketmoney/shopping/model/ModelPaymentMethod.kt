package com.example.pocketmoney.shopping.model

import com.example.pocketmoney.utils.myEnums.PaymentEnum

data class ModelPaymentMethod(
    val method: PaymentEnum,
    val methodName:String,
    var isSelected:Boolean?=false,
    val imageUrl:Any?=null
)
