package com.jmm.model.shopping_models

data class ModelPaymentCard(
    val cardHolderName:String,
    val cardNumber:String,
    val expiryMonth:Int,
    val expiryYear:Int,
    val cvv:Int?=null
)
