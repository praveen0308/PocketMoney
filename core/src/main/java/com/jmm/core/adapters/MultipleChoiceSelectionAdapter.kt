package com.jmm.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateMultiselectionItemBinding

class MultipleChoiceSelectionAdapter(private val mListener: MultipleChoiceSelectionInterface) :
    RecyclerView.Adapter<MultipleChoiceSelectionAdapter.MultipleChoiceSelectionViewHolder>() {

    private val mList = mutableListOf<Any>()
    private val checkedItems = mutableListOf<Boolean>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleChoiceSelectionViewHolder {
        return MultipleChoiceSelectionViewHolder(
            TemplateMultiselectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: MultipleChoiceSelectionViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setItemList(mList: List<Any>,checkedList:List<Boolean>) {
        this.mList.clear()
        this.mList.addAll(mList)
        this.checkedItems.clear()
        this.checkedItems.addAll(checkedList)
        notifyDataSetChanged()
    }

    fun getCheckedItems():List<Boolean>{
        return checkedItems
    }
    inner class MultipleChoiceSelectionViewHolder(
        val binding: TemplateMultiselectionItemBinding,
        private val mListener: MultipleChoiceSelectionInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                checkedItems[adapterPosition] = !checkedItems[adapterPosition]
                notifyItemChanged(adapterPosition)
            }
            /*binding.tvItem.setOnCheckedChangeListener { compoundButton, b ->
                checkedItems[adapterPosition] = !b
                notifyItemChanged(adapterPosition)
            }*/
        }

        fun bind(item: Any) {
            binding.apply {
                tvItem.text = item.toString()
                if (checkedItems[adapterPosition]){
                    cvIndicator.isChecked = true
                }
            }
        }
    }

    interface MultipleChoiceSelectionInterface {

    }
}