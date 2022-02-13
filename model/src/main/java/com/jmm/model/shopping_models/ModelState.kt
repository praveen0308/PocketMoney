package com.jmm.model.shopping_models

data class ModelState(
    val CountryCode: String,
    val ID: Int,
    val IsCancel: Boolean,
    val State1: String,
    val StateCode: String
){
    override fun toString(): String {
        return State1
    }
}