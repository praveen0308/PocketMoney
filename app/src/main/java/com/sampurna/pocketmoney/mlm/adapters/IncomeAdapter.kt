package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateSectionedRvBinding
import com.sampurna.pocketmoney.mlm.model.IncomeModel
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum


class IncomeAdapter(private val mListener: GrowthNCommissionAdapter.GrowthNCommissionInterface) :
    RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {


    private val mList = mutableListOf<IncomeModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        return IncomeViewHolder(
            TemplateSectionedRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setIncomeModelList(mList: List<IncomeModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class IncomeViewHolder(
        val binding: TemplateSectionedRvBinding,
        private val mListener: GrowthNCommissionAdapter.GrowthNCommissionInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }


        }

        fun bind(item: IncomeModel) {

            binding.apply {
                templateSectionedRvTitle.text = item.title
                if (item.id == NavigationEnum.GROWTH) {

                    templateSectionedRvRecyclerview.apply {
                        setHasFixedSize(true)

//                        val spanCount = lcm(1, 2)
                        val spanCount = 2
                        val layoutManager = GridLayoutManager(context, spanCount)
                        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                val numberOfColumns: Int = when (position) {
                                    0 -> 1
                                    1 -> 2
                                    else -> spanCount
                                }
                                return spanCount / numberOfColumns
                            }
                        }
                        this.layoutManager = layoutManager
                        val mAdapter =GrowthNCommissionAdapter(mListener)
                        mAdapter.setGrowthCommissionDataModelList(item.itemList)
                        adapter = mAdapter
                    }
                }
                else{
                    templateSectionedRvRecyclerview.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
//                        layoutManager = GridLayoutManager(context,2)

                        val mAdapter =GrowthNCommissionAdapter(mListener)
                        mAdapter.setGrowthCommissionDataModelList(item.itemList)
                        adapter = mAdapter
                    }
                }
            }
        }
    }

    interface IncomeInterface {

    }


}