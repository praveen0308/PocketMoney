package com.jmm.model.shopping_models

data class ModelCity(
    val City1: String,
    val ID: Int,
    val IsCancel: Boolean,
    val StateCode: String
){
    override fun toString(): String {
        return City1
    }
}