package com.example.pocketmoney.mlm.model.serviceModels

import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.google.gson.annotations.SerializedName

data class Records(
        @SerializedName("2G")
        val Plan2G: List<MobileOperatorPlan> = emptyList(),

        @SerializedName("3G/4G")
        val Plan3G_4G: List<MobileOperatorPlan> = emptyList(),

        val COMBO: List<MobileOperatorPlan> = emptyList(),

        @SerializedName("RATE CUTTER")
        val RATE_CUTTER: List<MobileOperatorPlan> = emptyList(),

        val SMS: List<MobileOperatorPlan> = emptyList(),

        @SerializedName("TOPUP")
        val TOP_UP: List<MobileOperatorPlan> = emptyList(),

        var specialPlanList : List<MobileOperatorPlan> = emptyList()
)