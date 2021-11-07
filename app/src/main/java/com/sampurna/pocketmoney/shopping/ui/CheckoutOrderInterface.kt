package com.sampurna.pocketmoney.shopping.ui

interface CheckoutOrderInterface {
    fun onDeliveryAddressSelected(addressId:Int)
    fun updateCheckOutStepStatus(step:Int)
    fun setPriceDetailNAction(amountPayable:Double)
}