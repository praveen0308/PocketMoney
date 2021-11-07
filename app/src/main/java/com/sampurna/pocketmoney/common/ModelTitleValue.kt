package com.sampurna.pocketmoney.common

import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum

data class ModelTitleValue(
    val title:String,
    val mValue:String,
    val mType : NavigationEnum = NavigationEnum.WALLET
)
