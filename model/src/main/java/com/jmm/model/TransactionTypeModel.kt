package com.jmm.model

data class TransactionTypeModel(
    val Comm_Cat_ID: Int,
    val Comm_Category_Name: String,
    val Is_Active: Boolean,
    val isSelected:Boolean=false
)