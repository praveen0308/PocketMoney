package com.sampurna.pocketmoney.mlm.model

data class UniversalFilterItemModel(
        val categoryId: Int=0,
        val type: Any = 0,
        val ID: Any = 0,
        val displayText: String,
        var isSelected: Boolean = false
){
        override fun toString(): String {
                return displayText
        }
}
