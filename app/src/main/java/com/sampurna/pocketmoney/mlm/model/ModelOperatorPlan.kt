package com.sampurna.pocketmoney.mlm.model

import com.sampurna.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan

data class ModelOperatorPlan(
        var title:String,
        var plansList:List<MobileOperatorPlan>?= listOf()
)
