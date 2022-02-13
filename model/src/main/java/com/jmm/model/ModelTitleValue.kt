package com.jmm.model

import com.jmm.model.myEnums.NavigationEnum


data class ModelTitleValue(
    val title:String,
    val mValue:String,
    val mType : NavigationEnum = NavigationEnum.WALLET
)
