package com.example.pocketmoney.mlm.model

import android.graphics.Bitmap
import com.example.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.example.pocketmoney.mlm.model.serviceModels.MobileCircleOperator
import java.io.Serializable

data class ModelContact(
        var contactName:String? = "",
        var contactNumber:String? ="",
        var image : String? = null,
        var operator:String?="Jio",
        var circle:String?="Mumbai"
):Serializable
