package com.sampurna.pocketmoney.mlm.adapters

import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.mlm.model.mlmModels.UpdateHistory
import com.sampurna.pocketmoney.utils.IntToOrdinal
import com.sampurna.pocketmoney.utils.SDF_d_M_y
import com.sampurna.pocketmoney.utils.convertISOTimeToAny
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

class CustomerUpdateCountAdapter(private val items: List<UpdateHistory>) :
    SequenceAdapter<UpdateHistory>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): UpdateHistory {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: UpdateHistory) {
        with(sequenceStep) {

            setActive(item.isActive)
            setAnchor(convertISOTimeToAny(item.UpdateDate, SDF_d_M_y))
            setAnchorTextAppearance(R.style.Base_TextAppearance_AppCompat_Small)
            setTitle(IntToOrdinal(item.CustomerRank.toInt()))
            setTitleTextAppearance(R.style.Base_TextAppearance_AppCompat_Display1)
        }
    }


}