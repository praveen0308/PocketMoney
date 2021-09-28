package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateCardWidgetBinding
import com.example.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel

class GrowthCommissionAdapter(private val growthCommissionAdapterInterface: GrowthCommissionAdapterInterface) :
    RecyclerView.Adapter<GrowthCommissionAdapter.GrowthCommissionViewHolder>() {

    private val itemList = mutableListOf<GrowthCommissionDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrowthCommissionViewHolder {
        return GrowthCommissionViewHolder(
            TemplateCardWidgetBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), growthCommissionAdapterInterface
        )
    }

    override fun onBindViewHolder(holder: GrowthCommissionViewHolder, position: Int) {

        holder.createMenuItem(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    fun setCommissionMenuItemList(itemList: List<GrowthCommissionDataModel>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()

    }

    inner class GrowthCommissionViewHolder(
        val binding: TemplateCardWidgetBinding,
        private val mListener: GrowthCommissionAdapterInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnClickListener {
                for (i in 0 until itemList.size) {
                    itemList[i].isSelected = i == absoluteAdapterPosition
                }
                mListener.onItemClick(itemList[absoluteAdapterPosition])
                notifyDataSetChanged()
            }
        }

        fun createMenuItem(item: GrowthCommissionDataModel) {
            binding.apply {
                tvSubtitle.text = item.title
//                tvSubtitle.text = item.subtitle
                tvValue.text = item.count.toString()
//                if (item.isSelected) {
//                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.Green)
//                    root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green_20))
//                } else {
//                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.material_on_surface_stroke)
//                    root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white)
//                    )
//                }
            }
        }
    }

    interface GrowthCommissionAdapterInterface {
        fun onItemClick(item: GrowthCommissionDataModel)
    }

}