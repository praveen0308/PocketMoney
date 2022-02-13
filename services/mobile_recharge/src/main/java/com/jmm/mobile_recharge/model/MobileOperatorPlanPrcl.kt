package com.jmm.mobile_recharge.model

import android.os.Parcelable
import com.jmm.model.serviceModels.MobileOperatorPlan
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MobileOperatorPlanPrcl(

    val desc: String?,
    val last_update: String? = "NA",
    val rs: String?,
    val validity: String? = "NA"
) : Parcelable {

    fun toMobileOperatorPlan(): MobileOperatorPlan {
        return MobileOperatorPlan(this.desc,this.last_update,this.rs,this.validity)
    }

}
