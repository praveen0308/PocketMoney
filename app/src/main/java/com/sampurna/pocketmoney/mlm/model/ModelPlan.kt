package com.sampurna.pocketmoney.mlm.model

import android.os.Parcel
import android.os.Parcelable

data class ModelPlan(
        val Talktime: Double,
        val Validity:String?,
        val Data: String?,
        val Amount: Int
):Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readDouble(),
                parcel.readString(),
                parcel.readString(),
                parcel.readInt()
        ) {
        }

        override fun describeContents(): Int {
                TODO("Not yet implemented")
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                TODO("Not yet implemented")
        }

        companion object CREATOR : Parcelable.Creator<ModelPlan> {
                override fun createFromParcel(parcel: Parcel): ModelPlan {
                        return ModelPlan(parcel)
                }

                override fun newArray(size: Int): Array<ModelPlan?> {
                        return arrayOfNulls(size)
                }
        }
}
