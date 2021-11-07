package com.sampurna.pocketmoney.mlm

import com.sampurna.pocketmoney.mlm.model.RechargeEnum
import com.sampurna.pocketmoney.utils.myEnums.MyEnums

interface HomeParentItemListener {
    fun onItemClick(viewType: MyEnums, action:RechargeEnum)
}