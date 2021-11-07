package com.sampurna.pocketmoney.utils.MyCustomNavigationDrawer

import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum

data class ModelItem(
        val title:String,
        var subItemList:List<ModelSubItem>,
        val action:NavigationEnum,
        var isExpanded:Boolean = false

)
