package com.example.pocketmoney.mlm.model

import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan

data class ModelOperatorPlan(
        var title:String,
        var plansList:List<MobileOperatorPlan>?= listOf()
)
