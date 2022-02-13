package com.jmm.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateSocialItemBinding
import com.jmm.model.SocialLinkModel

class SocialItemAdapter(private val mListener: SocialItemInterface) :
    RecyclerView.Adapter<SocialItemAdapter.SocialItemViewHolder>() {


    private val mList = mutableListOf<SocialLinkModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialItemViewHolder {
        return SocialItemViewHolder(
            TemplateSocialItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: SocialItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setSocialLinkModelList(mList: List<SocialLinkModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class SocialItemViewHolder(
        val binding: TemplateSocialItemBinding,
        private val mListener: SocialItemInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mList[adapterPosition])
            }


        }

        fun bind(item: SocialLinkModel) {
            binding.apply {
                icon.setImageResource(item.icon)
            }
        }
    }

    interface SocialItemInterface {
        fun onItemClick(item: SocialLinkModel)
    }
}