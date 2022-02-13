package com.jmm.model.mlmModels

data class UpdateHistory(
    val CustomerRank: String,
    val UpdateDate: String,
    val UserID: String,

    // custom
    val isActive : Boolean = false
)