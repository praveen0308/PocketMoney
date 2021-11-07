package com.sampurna.pocketmoney.mlm.model

import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum

data class ModelMenuItem(
        val id: NavigationEnum,
        val title:String,
        val description:String,
        val iconImage:Int
)
