package com.sampurna.pocketmoney.mlm.model

data class UniversalFilterModel(
        val id: Int,
        val title: String,
        var filterList: MutableList<UniversalFilterItemModel>,
        val layoutManager: Int = 0,
        val spanCount: Int = 0
)
