package com.jmm.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateCardWidgetBinding
import com.jmm.model.ModelTitleValue

class DashboardItemAdapter(private val mListener: DashboardItemInterface) :
    RecyclerView.Adapter<DashboardItemAdapter.DashboardItemViewHolder>() {


    private val mList = mutableListOf<ModelTitleValue>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        return DashboardItemViewHolder(
            TemplateCardWidgetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setModelTitleValueList(mList: List<ModelTitleValue>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class DashboardItemViewHolder(
        val binding: TemplateCardWidgetBinding,
        private val mListener: DashboardItemInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mList[adapterPosition])
            }


        }

        fun bind(item: ModelTitleValue) {
            binding.apply {
                tvSubtitle.text = item.title
                tvValue.text = item.mValue
            }
        }
    }

    interface DashboardItemInterface {
        fun onItemClick(item: ModelTitleValue)
    }


}