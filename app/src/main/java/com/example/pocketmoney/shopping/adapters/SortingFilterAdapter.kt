package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplateSingleSelectionDialogItemBinding
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel

class SortingFilterAdapter(private val sortingFilterItemListener: SortingFilterItemListener):RecyclerView.Adapter<SortingFilterAdapter.SortingFilterViewHolder>() {
    private val itemList = mutableListOf<UniversalFilterItemModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortingFilterViewHolder {
        return SortingFilterViewHolder(TemplateSingleSelectionDialogItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),sortingFilterItemListener)
    }

    override fun onBindViewHolder(holder: SortingFilterViewHolder, position: Int) {
        holder.createItem(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItemList(mList:MutableList<UniversalFilterItemModel>){
        itemList.clear()
        itemList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class SortingFilterViewHolder(
        val binding: TemplateSingleSelectionDialogItemBinding,
        private val mListener: SortingFilterItemListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                for (i in 0 until itemList.size) {
                    if (i == absoluteAdapterPosition) {
                        itemList[i].isSelected = true

                        mListener.onItemClick(itemList[i])

                    } else {
                        itemList[i].isSelected = false
                    }
                }


                notifyDataSetChanged()
            }
        }

        fun createItem(item: UniversalFilterItemModel) {
            binding.tvItemDisplayText.text = item.displayText
            if (item.isSelected == true) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorPrimary
                    )
                )
                binding.tvItemDisplayText.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorWhite
                    )
                )
            } else {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorWhite
                    )
                )
                binding.tvItemDisplayText.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorTextPrimary
                    )
                )

            }
        }
    }


    interface SortingFilterItemListener{
        fun onItemClick(item: UniversalFilterItemModel)


    }

}