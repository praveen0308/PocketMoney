package com.sampurna.pocketmoney.mlm.model

import com.sampurna.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum

data class IncomeModel(
    val title:String,
    val id : NavigationEnum,
    val itemList : List<GrowthCommissionDataModel>
)
