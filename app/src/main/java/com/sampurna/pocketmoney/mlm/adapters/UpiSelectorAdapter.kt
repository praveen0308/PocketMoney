package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateUpiCodeOptionsBinding
import com.sampurna.pocketmoney.mlm.model.UPIModel

class UpiSelectorAdapter(private val mListener: UpiSelectorInterface) :
    RecyclerView.Adapter<UpiSelectorAdapter.UpiSelectorViewHolder>() {


    private val mList = mutableListOf<UPIModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpiSelectorViewHolder {
        return UpiSelectorViewHolder(
            TemplateUpiCodeOptionsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: UpiSelectorViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setUPIModelList(mList: List<UPIModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class UpiSelectorViewHolder(
        val binding: TemplateUpiCodeOptionsBinding,
        private val mListener: UpiSelectorInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mList[absoluteAdapterPosition])
            }


        }

        fun bind(item: UPIModel) {
            binding.apply {
                tvTitle.text = item.title
                if (item.iconUrl == 0) {
                    ivIcon.isVisible = false
                } else {
                    ivIcon.isVisible = true
                    ivIcon.setImageResource(item.iconUrl)
                }
            }
        }
    }

    interface UpiSelectorInterface {
        fun onItemClick(item: UPIModel)
    }


}