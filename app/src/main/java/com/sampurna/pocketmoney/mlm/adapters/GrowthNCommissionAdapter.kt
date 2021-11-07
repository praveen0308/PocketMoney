package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateCardWidgetType2Binding
import com.sampurna.pocketmoney.databinding.TemplateSystemGrowthBinding
import com.sampurna.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum


class GrowthNCommissionAdapter(private val mListener: GrowthNCommissionInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mList = mutableListOf<GrowthCommissionDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1->{
                SystemGrowthViewHolder(
                    TemplateSystemGrowthBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), mListener
                )
            }
           else->{
               GrowthNCommissionViewHolder(
                   TemplateCardWidgetType2Binding.inflate(
                       LayoutInflater.from(parent.context),
                       parent,
                       false
                   ), mListener
               )
           }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            1-> (holder as SystemGrowthViewHolder).bind(mList[position])
            else->(holder as GrowthNCommissionViewHolder).bind(mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(mList[position].subType){
            NavigationEnum.SYSTEM_GROWTH->1
            else->2
        }
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


    inner class SystemGrowthViewHolder(
        val binding: TemplateSystemGrowthBinding,
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
                /*cpvGrowth.progress =(item.count/50).toFloat()
                cpvGrowth.text = "${item.count.toInt()}/50"*/
            }
        }
    }

    interface GrowthNCommissionInterface {
        fun onItemClick(item: GrowthCommissionDataModel)
    }


}