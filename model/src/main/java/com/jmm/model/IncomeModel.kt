package com.jmm.model

import com.jmm.model.mlmModels.GrowthCommissionDataModel
import com.jmm.model.myEnums.NavigationEnum


data class IncomeModel(
    val title:String,
    val id : NavigationEnum,
    val itemList : List<GrowthCommissionDataModel>
)
