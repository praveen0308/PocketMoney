package com.jmm.model.mlmModels

import com.jmm.model.myEnums.NavigationEnum


data class GrowthCommissionDataModel(
    val id:Int,
    val title:String,
    val count:Double,
    var commissionDataList:List<Any>?=null,
    var isSelected:Boolean = false,
    val type:NavigationEnum = NavigationEnum.GROWTH,
    val subType:NavigationEnum = NavigationEnum.NONE

)
