package com.sampurna.pocketmoney.shopping.model

import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum

data class ModelPaymentMethod(
    val method: PaymentEnum,
    val methodName:String,
    val imageUrl:Int,
    var isSelected:Boolean?=false,
)
