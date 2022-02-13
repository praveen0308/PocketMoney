package com.jmm.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateTitleSubtitleVerticalBinding
import com.jmm.model.ModelTitleValue

class TransactionDetailsAdapter() :
    RecyclerView.Adapter<TransactionDetailsAdapter.TransactionDetailsViewHolder>() {


    private val mList = mutableListOf<ModelTitleValue>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionDetailsViewHolder {
        return TransactionDetailsViewHolder(
            TemplateTitleSubtitleVerticalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionDetailsViewHolder, position: Int) {
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

    inner class TransactionDetailsViewHolder(
        val binding: TemplateTitleSubtitleVerticalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }


        }

        fun bind(item: ModelTitleValue) {
            binding.apply {
                tvTitle.text = item.title
                tvSubtitle.text = item.mValue
            }
        }
    }

}