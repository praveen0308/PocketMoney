package com.jmm.model

import com.jmm.model.serviceModels.MobileOperatorPlan

data class ModelOperatorPlan(
        var title:String,
        var plansList:List<MobileOperatorPlan>?= listOf()
)
