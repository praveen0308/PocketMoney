package com.example.pocketmoney.mlm.model

import com.example.pocketmoney.utils.myEnums.NavigationEnum

data class ModelMenuItem(
        val id: NavigationEnum,
        val title:String,
        val description:String,
        val iconImage:Int
)
