package com.example.pocketmoney.mlm.model.payoutmodels

data class BankModel(
    val BankName: String? = null,
    val ID: Int? = null,
    val IFSC: Any? = null,
    val IsCancel: Boolean? = null,
    val ShortCode: String? = null
){

    override fun toString(): String {
        return BankName.toString()
    }
}

