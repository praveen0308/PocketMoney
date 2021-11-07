package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateRechargeHistoryItemBinding
import com.sampurna.pocketmoney.mlm.model.serviceModels.RechargeHistoryModel
import com.sampurna.pocketmoney.utils.getOperatorLogo

class RechargeHistoryAdapter(private val mListener: RechargeHistoryInterface) :
    RecyclerView.Adapter<RechargeHistoryAdapter.RechargeHistoryViewHolder>() {


    private val mList = mutableListOf<RechargeHistoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargeHistoryViewHolder {
        return RechargeHistoryViewHolder(
            TemplateRechargeHistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: RechargeHistoryViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setRechargeHistoryModelList(mList: List<RechargeHistoryModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class RechargeHistoryViewHolder(
        val binding: TemplateRechargeHistoryItemBinding,
        private val mListener: RechargeHistoryInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRecharge.setOnClickListener {
                mListener.onRechargeClick(mList[absoluteAdapterPosition])
            }


        }

        fun bind(item: RechargeHistoryModel) {
            binding.apply {
                tvRechargeAmount.text = "₹ ${item.RechargeAmt.toString()}"
                tvRechargeDate.text = item.RequestDate
                ivOperatorLogo.setImageResource(getOperatorLogo(item.OperatorProvider!!.toString()))
                tvRechargeNumber.setText("${item.MobileNo?.substring(0,item.MobileNo.length-2)}")
            }
        }
    }

    interface RechargeHistoryInterface {
        fun onRechargeClick(item: RechargeHistoryModel)
    }


}