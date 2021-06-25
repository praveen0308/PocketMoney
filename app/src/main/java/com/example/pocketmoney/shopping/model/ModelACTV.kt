package com.example.pocketmoney.shopping.model

data class ModelACTV(
        val itemId:Int,
        val name:String
){
    override fun toString(): String {
        return name
    }
}

