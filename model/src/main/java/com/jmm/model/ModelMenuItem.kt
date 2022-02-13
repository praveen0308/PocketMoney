package com.jmm.model

import com.jmm.model.myEnums.NavigationEnum


data class ModelMenuItem(
        val id: NavigationEnum,
        val title:String,
        val description:String,
        val iconImage:Int
)
