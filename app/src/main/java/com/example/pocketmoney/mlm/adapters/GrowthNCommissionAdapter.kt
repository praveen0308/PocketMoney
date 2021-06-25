package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateCardWidgetType2Binding
import com.example.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel
import com.example.pocketmoney.utils.myEnums.NavigationEnum


class GrowthNCommissionAdapter(private val mListener: GrowthNCommissionInterface) :
    RecyclerView.Adapter<GrowthNCommissionAdapter.GrowthNCommissionViewHolder>() {


    private val mList = mutableListOf<GrowthCommissionDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrowthNCommissionViewHolder {
        return GrowthNCommissionViewHolder(
            TemplateCardWidgetType2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: GrowthNCommissionViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setGrowthCommissionDataModelList(mList: List<GrowthCommissionDataModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class GrowthNCommissionViewHolder(
        val binding: TemplateCardWidgetType2Binding,
        private val mListener: GrowthNCommissionInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mList[absoluteAdapterPosition])
            }


        }

        fun bind(item: GrowthCommissionDataModel) {
            binding.apply {
                tvTitle.text = item.title
                if (item.type==NavigationEnum.GROWTH){
                    tvValue.text = item.count.toInt().toString()
                }else{
                    tvValue.text = "₹ ${item.count}"
                }


            }
        }
    }

    interface GrowthNCommissionInterface {
        fun onItemClick(item: GrowthCommissionDataModel)
    }


}