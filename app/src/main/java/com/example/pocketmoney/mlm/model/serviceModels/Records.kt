package com.example.pocketmoney.mlm.model.serviceModels

import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.google.gson.annotations.SerializedName

data class Records(
        @SerializedName("2G")
        val Plan2G: List<MobileOperatorPlan>,

        @SerializedName("3G/4G")
        val Plan3G_4G: List<MobileOperatorPlan>,

        val COMBO: List<MobileOperatorPlan>,

        @SerializedName("RATE CUTTER")
        val RATE_CUTTER: List<MobileOperatorPlan>,

        val SMS: List<MobileOperatorPlan>,

        @SerializedName("TOPUP")
        val TOP_UP: List<MobileOperatorPlan>,

        var specialPlanList : List<MobileOperatorPlan>
)