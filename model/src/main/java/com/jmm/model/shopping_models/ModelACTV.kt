package com.jmm.model.shopping_models

data class ModelACTV(
        val itemId:Int,
        val name:String
){
    override fun toString(): String {
        return name
    }
}

