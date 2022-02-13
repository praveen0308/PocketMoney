package com.jmm.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.R
import com.jmm.core.databinding.TemplatePaymentHistoryType2Binding
import com.jmm.core.utils.convertISOTimeToDateTime
import com.jmm.model.TransactionModel

class CustomerTransactionHistoryAdapter(private val growthCommissionAdapterInterface: CustomerTransactionHistoryAdapterInterface) :
        RecyclerView.Adapter<CustomerTransactionHistoryAdapter.CustomerTransactionHistoryViewHolder>() {

    private val itemList = mutableListOf<TransactionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerTransactionHistoryViewHolder {
        return CustomerTransactionHistoryViewHolder(
            TemplatePaymentHistoryType2Binding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                ), growthCommissionAdapterInterface
        )


    }

    override fun onBindViewHolder(holder: CustomerTransactionHistoryViewHolder, position: Int) {

        holder.createPaymentHistoryItem(itemList[position])


    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    fun setTransactionHistory(itemList: List<TransactionModel>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()

    }

    inner class CustomerTransactionHistoryViewHolder(
            val binding: TemplatePaymentHistoryType2Binding,
            private val mListener: CustomerTransactionHistoryAdapterInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnClickListener {
                mListener.onTransactionHistoryItemClick(itemList[adapterPosition])
            }
        }
        fun createPaymentHistoryItem(transactionModel: TransactionModel){
            binding.apply {
                tvSubTitle.text = transactionModel.Trans_Category
                if (transactionModel.Credit!=0.0){

                    ivIcon.setImageResource(R.drawable.ic_round_call_received_24)
                    tvMainTitle.text = "Received from"
                    tvSecondaryTitle.text = "+₹".plus(transactionModel.Credit)
//                    tvSecondaryTitle.setTextColor(ContextCompat.getColor(root.context,R.color.LightGreen))
                }
                if (transactionModel.Debit!=0.0){
                    ivIcon.setImageResource(R.drawable.ic_round_call_made_24)
                    tvMainTitle.text = "Paid to"
                    tvSecondaryTitle.text = "-₹".plus(transactionModel.Debit)
//                    tvSecondaryTitle.setTextColor(ContextCompat.getColor(root.context,R.color.CreamyRed))
                }

                tvDate.text = convertISOTimeToDateTime(transactionModel.Trans_Date)
            }
        }
    }

    interface CustomerTransactionHistoryAdapterInterface {
        fun onTransactionHistoryItemClick(item: TransactionModel)
    }


}