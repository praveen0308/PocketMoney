package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateCustomerCommissionBinding
import com.sampurna.pocketmoney.mlm.model.mlmModels.CommissionHistoryModel
import com.sampurna.pocketmoney.mlm.model.mlmModels.GrowthHistory
import com.sampurna.pocketmoney.mlm.model.mlmModels.UpdateHistory

class GrowthCommissionHistoryAdapter(private val growthCommissionAdapterInterface: GrowthCommissionHistoryAdapterInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemList = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1->GrowthCommissionHistoryViewHolder(
                    TemplateCustomerCommissionBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    ), growthCommissionAdapterInterface
            )
            2->SystemGrowthHistoryViewHolder(
                    TemplateCustomerCommissionBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    ))
            3->UpdateCountViewHolder(
                    TemplateCustomerCommissionBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    ))

            else->GrowthCommissionHistoryViewHolder(
                    TemplateCustomerCommissionBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    ), growthCommissionAdapterInterface
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            1->(holder as GrowthCommissionHistoryViewHolder).createMenuItem(itemList[position] as CommissionHistoryModel)
            2->(holder as SystemGrowthHistoryViewHolder).createItem(itemList[position] as GrowthHistory)
            3->(holder as UpdateCountViewHolder).createItem(itemList[position] as UpdateHistory)
            0->(holder as GrowthCommissionHistoryViewHolder).createMenuItem(itemList[position] as CommissionHistoryModel)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(itemList[position].javaClass.simpleName){
            CommissionHistoryModel::class.java.simpleName->1
            GrowthHistory::class.java.simpleName->2
            UpdateHistory::class.java.simpleName->3
            else->0
        }

    }

    fun setCommissionDataItemList(itemList: List<Any>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()

    }

    inner class GrowthCommissionHistoryViewHolder(
        val binding: TemplateCustomerCommissionBinding,
        private val mListener: GrowthCommissionHistoryAdapterInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnClickListener {
                mListener.onCommissionHistoryItemClick(itemList[absoluteAdapterPosition] as CommissionHistoryModel)
            }
        }

        fun createMenuItem(item: CommissionHistoryModel) {
            binding.apply {
              /*  tvReferenceId.text = item.ReferenceID
                tvMainTitle.text = item.FullName
                tvSubTitle.text = convertISOTimeToDateTime(item.OriginatedOn)
                tvAmount.text = "₹ ${item.CommCreditAmt}"*/
            }
        }
    }

    inner class SystemGrowthHistoryViewHolder(
            val binding: TemplateCustomerCommissionBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun createItem(item:GrowthHistory){
            binding.apply {
                /*tvReferenceId.text = item.Reference_Id
                tvMainTitle.text = item.Trans_Category
                tvSubTitle.text = convertISOTimeToDateTime(item.Trans_Date)
                tvAmount.text = if (item.Credit == 0.0){
                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Green))
                    item.Debit.toString()}
                else
                {
                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Red))
                    item.Credit.toString()}

*/
            }

        }
    }

    inner class UpdateCountViewHolder(
            val binding: TemplateCustomerCommissionBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun createItem(item:UpdateHistory){
            binding.apply {

               /* tvMainTitle.text = item.UserID
                tvSubTitle.text = convertISOTimeToDateTime(item.UpdateDate)
                tvAmount.text = item.CustomerRank
*/
            }

        }
    }

    interface GrowthCommissionHistoryAdapterInterface {
        fun onCommissionHistoryItemClick(item: CommissionHistoryModel)
    }



}