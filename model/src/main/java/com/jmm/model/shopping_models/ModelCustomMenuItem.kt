package com.jmm.model.shopping_models

import com.jmm.model.myEnums.MenuEnum


data class ModelCustomMenuItem(
    val title:String,
    val subtitle:String,
    val action: MenuEnum
)
