package com.sampurna.pocketmoney.mlm.model

data class ModelServiceCategory(
    val categoryName:String = "",
    val serviceViewList:List<ModelServiceView> = ArrayList<ModelServiceView>()
)
