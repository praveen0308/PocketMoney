package com.jmm.model.shopping_models

data class DiscountCouponModel(
    val Amount: Double? = null,
    val CouponCode: String? = null,
    val Description: String? = null,
    val EndDate: String? = null,
    val IsFixed: Boolean? = null,
    val IsRedeemAllowed: Boolean? = null,
    val IssuedOn: String? = null,
    val Name: String? = null,
    val RedeemDescription: String? = null,
    val UsedDetails: String? = null,
    val UserID: String? = null,
    var isSelected:Boolean=false
)