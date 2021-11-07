package com.sampurna.pocketmoney.shopping.model

import com.sampurna.pocketmoney.utils.myEnums.MenuEnum

data class ModelCustomMenuItem(
    val title:String,
    val subtitle:String,
    val action: MenuEnum
)
