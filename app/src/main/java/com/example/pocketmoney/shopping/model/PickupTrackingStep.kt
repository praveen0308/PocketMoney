package com.example.pocketmoney.shopping.model

/*

Author : Praveen A. Yadav
Created On : 03:33 22-06-2021

*/

data class OrderTrackingStep(
    val title:String,
    val subtitle:String?=null,
    var timestamp:String,
    var isActive:Boolean=false,
)
