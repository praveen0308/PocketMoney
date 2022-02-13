package com.jmm.mobile_recharge.model

import com.jmm.model.serviceModels.MobileOperatorPlan

fun fromMobileOperatorPlan(mobileOperatorPlan: MobileOperatorPlan):MobileOperatorPlanPrcl{
    return MobileOperatorPlanPrcl(mobileOperatorPlan.desc,mobileOperatorPlan.last_update,mobileOperatorPlan.rs,mobileOperatorPlan.validity)
}