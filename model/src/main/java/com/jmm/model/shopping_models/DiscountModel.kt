package com.jmm.model.shopping_models

data class DiscountModel(
    val Amount: Double = 0.0,
    val Applied_IDs: String? = null,
    val Code: String = "",
    val Description: String? = null,
    val Ends_At: String? = null,
    val Id: Int? = null,
    val IsCategory: Boolean? = null,
    val IsFixed: Boolean=false ,
    val IsMembers: Boolean? = null,
    val IsProduct: Boolean? = null,
    val IsRedeemAllowed: Boolean? = null,
    val IsSubCategory: Boolean? = null,
    val Max_Discount_Amount: Double? = null,
    val Max_Uses: Int? = null,
    val Min_Order_Value: Double? = null,
    val Name: String? = null,
    val Starts_At: String? = null,
    val Type: Int? = null,
    var isSelected:Boolean = false
)