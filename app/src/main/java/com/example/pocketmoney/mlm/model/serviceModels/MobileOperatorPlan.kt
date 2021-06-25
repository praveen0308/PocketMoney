package com.example.pocketmoney.mlm.model.serviceModels

import android.os.Parcel
import android.os.Parcelable

data class MobileOperatorPlan(
        val desc: String?,
        val last_update: String? ="NA",
        val rs: String?,
        val validity: String? ="NA"
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(desc)
        parcel.writeString(last_update)
        parcel.writeString(rs)
        parcel.writeString(validity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MobileOperatorPlan> {
        override fun createFromParcel(parcel: Parcel): MobileOperatorPlan {
            return MobileOperatorPlan(parcel)
        }

        override fun newArray(size: Int): Array<MobileOperatorPlan?> {
            return arrayOfNulls(size)
        }
    }
}