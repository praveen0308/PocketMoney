package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateMultiSelectionDialogItemBinding
import com.sampurna.pocketmoney.databinding.TemplateSingleSelectionDialogItemBinding
import com.sampurna.pocketmoney.mlm.model.UniversalFilterItemModel
import com.sampurna.pocketmoney.utils.myEnums.PaymentHistoryFilterEnum

class PaymentHistoryFilterItemAdapter(
        private val paymentHistoryDialogListener: PaymentHistoryDialogListener,
        private val itemList: MutableList<UniversalFilterItemModel>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.template_single_selection_dialog_item -> SingleSelectionViewHolder(
                    TemplateSingleSelectionDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), paymentHistoryDialogListener)
            R.layout.template_multi_selection_dialog_item -> MultiSelectionViewHolder(
                    TemplateMultiSelectionDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> SingleSelectionViewHolder(
                    TemplateSingleSelectionDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), paymentHistoryDialogListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.template_single_selection_dialog_item -> (holder as SingleSelectionViewHolder).createItem(itemList[position])
            R.layout.template_multi_selection_dialog_item -> (holder as MultiSelectionViewHolder).createItem(itemList[position])
            else -> (holder as SingleSelectionViewHolder).createItem(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type) {
            PaymentHistoryFilterEnum.SINGLE -> R.layout.template_single_selection_dialog_item
            PaymentHistoryFilterEnum.MULTI -> R.layout.template_multi_selection_dialog_item
            else -> R.layout.template_single_selection_dialog_item
        }
    }

    fun setItemList(itemList: MutableList<UniversalFilterItemModel>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class SingleSelectionViewHolder(val binding: TemplateSingleSelectionDialogItemBinding, private val paymentHistoryDialogListener: PaymentHistoryDialogListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                for (i in 0 until itemList.size) {
                    if (i == absoluteAdapterPosition) {
                        itemList[i].isSelected = true
                        paymentHistoryDialogListener.onSingleItemClick(itemList[i].categoryId,itemList)
                    } else {
                        itemList[i].isSelected = false
                    }
                }
                notifyDataSetChanged()
            }
        }

        fun createItem(item: UniversalFilterItemModel) {
//            binding.tvItemDisplayText.text = item.displayText
            binding.apply {
                rbFilterItem.apply {
                    text = item.displayText

                    isChecked = item.isSelected == true
                }
            }

        }
    }

    inner class MultiSelectionViewHolder(val binding: TemplateMultiSelectionDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->

                itemList[absoluteAdapterPosition].isSelected = isChecked

                paymentHistoryDialogListener.onSingleItemClick(itemList[absoluteAdapterPosition].categoryId,itemList)
            }
        }

        fun createItem(item: UniversalFilterItemModel) {
            binding.tvItemText.text = item.displayText
            binding.checkBox.text = item.displayText
            binding.checkBox.isChecked = item.isSelected == true
        }
    }

    interface PaymentHistoryDialogListener {
        fun onSingleItemClick(index:Int,itemList: MutableList<UniversalFilterItemModel>)
    }
}