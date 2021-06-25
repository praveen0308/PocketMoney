package com.example.pocketmoney.mlm

import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.utils.myEnums.MyEnums

interface HomeParentItemListener {
    fun onItemClick(viewType: MyEnums, action:RechargeEnum)
}