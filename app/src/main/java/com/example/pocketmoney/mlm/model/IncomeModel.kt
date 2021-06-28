package com.example.pocketmoney.mlm.model

import com.example.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel
import com.example.pocketmoney.utils.myEnums.NavigationEnum

data class IncomeModel(
    val title:String,
    val id : NavigationEnum,
    val itemList : List<GrowthCommissionDataModel>
)
