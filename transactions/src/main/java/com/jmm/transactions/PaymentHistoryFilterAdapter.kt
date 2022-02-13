package com.jmm.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateSectionedRvBinding
import com.jmm.model.UniversalFilterItemModel
import com.jmm.model.UniversalFilterModel

class PaymentHistoryFilterAdapter(
        private val paymentHistoryDialogListener: PaymentHistoryFilterItemAdapter.PaymentHistoryDialogListener
):RecyclerView.Adapter<PaymentHistoryFilterAdapter.PaymentHistoryFilterViewHolder>() {


    private val filterList = mutableListOf<UniversalFilterModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentHistoryFilterViewHolder {
        return PaymentHistoryFilterViewHolder(TemplateSectionedRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PaymentHistoryFilterViewHolder, position: Int) {
        holder.createFilerItems(filterList[position])
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun setFilterList(itemList:List<UniversalFilterModel>){
        this.filterList.clear()
        this.filterList.addAll(itemList)
        notifyDataSetChanged()
    }


    inner class PaymentHistoryFilterViewHolder(val binding: TemplateSectionedRvBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun createFilerItems(filterItem: UniversalFilterModel) {
            binding.apply {
                templateSectionedRvTitle.text = filterItem.title
                templateSectionedRvRecyclerview.apply {
                    setHasFixedSize(true)
                    layoutManager = when(filterItem.layoutManager){
                        0->LinearLayoutManager(context)
                        1->LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                        2->GridLayoutManager(context,filterItem.spanCount)
                        else->LinearLayoutManager(context)
                    }
                    adapter = PaymentHistoryFilterItemAdapter(paymentHistoryDialogListener,filterItem.filterList as MutableList<UniversalFilterItemModel>)
                }
            }


        }
    }

}