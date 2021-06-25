package com.example.pocketmoney.utils.MyCustomNavigationDrawer

import com.example.pocketmoney.utils.myEnums.NavigationEnum

data class ModelItem(
        val title:String,
        var subItemList:List<ModelSubItem>,
        val action:NavigationEnum,
        var isExpanded:Boolean = false

)
