package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateMasterRecyclerviewItemBinding

class CustomerGrowthAdapter(private val mListener: CustomerGrowthInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mList = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CustomerGrowthViewHolder(
            TemplateMasterRecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setAnyList(mList: List<Any>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class CustomerGrowthViewHolder(
        val binding: TemplateMasterRecyclerviewItemBinding,
        private val mListener: CustomerGrowthInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }


        }

        fun bind(item: Any) {
            binding.apply {

            }
        }
    }

    interface CustomerGrowthInterface {

    }


}