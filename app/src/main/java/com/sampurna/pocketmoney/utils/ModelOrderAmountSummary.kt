package com.sampurna.pocketmoney.utils

data class ModelOrderAmountSummary(
        var itemQuantity:Int=0,
        var productOldPrice:Double=0.0,
        var saving:Double=0.0,
        var totalPrice:Double=0.0,
        var shippingCharge:Double=0.0,
        var extraDiscount:Double=0.0,
        var tax:Double=0.0,
        var grandTotal:Double=0.0

)
