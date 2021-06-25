package com.example.pocketmoney.mlm.model.mlmModels

import com.example.pocketmoney.mlm.model.mlmModels.CommissionHistoryModel
import com.example.pocketmoney.utils.myEnums.NavigationEnum

data class GrowthCommissionDataModel(
    val id:Int,
    val title:String,
    val count:Double,
    var commissionDataList:List<Any>?=null,
    var isSelected:Boolean = false,
    val type:NavigationEnum = NavigationEnum.GROWTH,
    val subType:NavigationEnum = NavigationEnum.NONE

)
