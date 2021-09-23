package com.example.pocketmoney.shopping.adapters

import com.example.pocketmoney.R
import com.example.pocketmoney.shopping.model.OrderTrackingStep
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

/*

Author : Praveen A. Yadav
Created On : 03:29 22-06-2021

*/

class OrderTrackingAdapter(private val items: List<OrderTrackingStep>) : SequenceAdapter<OrderTrackingStep>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): OrderTrackingStep {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: OrderTrackingStep) {
        with(sequenceStep) {
            
            setActive(item.isActive)
            setAnchor(item.timestamp)
            setAnchorTextAppearance(R.style.Base_TextAppearance_AppCompat_Small)
            setTitle(item.title)
            setTitleTextAppearance(R.style.Base_TextAppearance_AppCompat_Body1)
            setSubtitle(item.subtitle)
            setSubtitleTextAppearance(R.style.Base_TextAppearance_AppCompat_Small)
        }
    }


}