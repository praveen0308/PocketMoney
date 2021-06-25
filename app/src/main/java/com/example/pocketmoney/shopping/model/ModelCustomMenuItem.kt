package com.example.pocketmoney.shopping.model

import com.example.pocketmoney.utils.myEnums.MenuEnum

data class ModelCustomMenuItem(
    val title:String,
    val subtitle:String,
    val action: MenuEnum
)
