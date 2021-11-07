package com.sampurna.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplatePrivacyPolicyItemsBinding
import com.sampurna.pocketmoney.shopping.model.ModelTitleSubtitle

class TitleTextAdapter() :
    RecyclerView.Adapter<TitleTextAdapter.TitleTextViewHolder>() {


    private val mList = mutableListOf<ModelTitleSubtitle>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleTextViewHolder {
        return TitleTextViewHolder(
            TemplatePrivacyPolicyItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TitleTextViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setModelTitleSubtitleList(mList: List<ModelTitleSubtitle>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class TitleTextViewHolder(
        val binding: TemplatePrivacyPolicyItemsBinding

    ) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(item: ModelTitleSubtitle) {
            binding.apply {
                tvTitle.text = item.title
                tvSubtitle.text = item.text
            }
        }
    }




}