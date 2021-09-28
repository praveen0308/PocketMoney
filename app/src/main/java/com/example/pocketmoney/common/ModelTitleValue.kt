package com.example.pocketmoney.common

import com.example.pocketmoney.utils.myEnums.NavigationEnum

data class ModelTitleValue(
    val title:String,
    val mValue:String,
    val mType : NavigationEnum = NavigationEnum.WALLET
)
