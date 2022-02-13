package com.jmm.kyc

data class DocumentType(
    val documentId:Int,
    val documentName:String,
    var isSelected:Boolean = false
)
