package com.jmm.model

import java.io.Serializable

data class ModelContact(
        var contactName:String? = "",
        var contactNumber:String? ="",
        var image : String? = null,
        var operator:String?="Jio",
        var circle:String?="Mumbai"
):Serializable
