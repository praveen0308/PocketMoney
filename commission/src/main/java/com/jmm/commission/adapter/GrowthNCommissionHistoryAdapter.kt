package com.jmm.commission.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateMasterRecyclerviewItemBinding
import com.jmm.core.utils.SDF_d_M_y
import com.jmm.core.utils.convertISOTimeToAny
import com.jmm.core.utils.setAmount
import com.jmm.model.mlmModels.CommissionHistoryModel
import com.jmm.model.mlmModels.GrowthHistory



class GrowthNCommissionHistoryAdapter(private val mListener: GrowthNCommissionHistoryInterface) :
    RecyclerView.Adapter<GrowthNCommissionHistoryAdapter.GrowthNCommissionHistoryViewHolder>() {


    private val mList = mutableListOf<Any>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GrowthNCommissionHistoryViewHolder {
        return GrowthNCommissionHistoryViewHolder(
            TemplateMasterRecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: GrowthNCommissionHistoryViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setAnyList(mList: List<Any>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class GrowthNCommissionHistoryViewHolder(
        val binding: TemplateMasterRecyclerviewItemBinding,
        private val mListener: GrowthNCommissionHistoryInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }


        }

        fun bind(item: Any) {
            binding.apply {
                when(item){
                    is GrowthHistory ->{
                        tvDate.text = convertISOTimeToAny(item.Trans_Date, SDF_d_M_y)
                        tvMainTitle.text = "Increased by"
                        tvSubTitle.text = item.Trans_Category
                        tvSecondaryTitle.setAmount(item.Credit)

                    }
                    is CommissionHistoryModel ->{
                        tvDate.text = convertISOTimeToAny(item.OriginatedOn, SDF_d_M_y)
                        tvMainTitle.text = "Originated by"
                        tvSubTitle.text = item.FullName
                        tvSecondaryTitle.setAmount(item.CommCreditAmt)

                    }

                }
            }
        }
    }

    interface GrowthNCommissionHistoryInterface {
        fun onItemClick(item:Any)
    }


}