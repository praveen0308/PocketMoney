package com.jmm.core

import com.jmm.model.RechargeEnum
import com.jmm.model.myEnums.MyEnums

interface HomeParentItemListener {
    fun onItemClick(viewType: MyEnums, action: RechargeEnum)
}