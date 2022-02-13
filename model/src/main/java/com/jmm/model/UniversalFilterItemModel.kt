package com.jmm.model

import com.jmm.model.myEnums.DateTimeEnum

data class UniversalFilterItemModel(
        val categoryId: Int=0,
        val type: Any = 0,
        val ID: DateTimeEnum = DateTimeEnum.LAST_MONTH,
        val displayText: String,
        var isSelected: Boolean = false
){
        override fun toString(): String {
                return displayText
        }
}
