package com.jmm.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplatePaymentHistoryType2Binding
import com.jmm.core.utils.convertISOTimeToDateTime
import com.jmm.model.TransactionModel

class PaymentHistoryAdapter(val paymentHistoryInterface: PaymentHistoryInterface):RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder>() {

    private val paymentsList = mutableListOf<TransactionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        return PaymentHistoryViewHolder(TemplatePaymentHistoryType2Binding.inflate(LayoutInflater.from(parent.context),parent,false),paymentHistoryInterface)
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        holder.createPaymentHistoryItem(paymentsList[position])
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }

    fun setTransactionHistoryList(paymentsList:List<TransactionModel>){
        this.paymentsList.clear()
        this.paymentsList.addAll(paymentsList)
        notifyDataSetChanged()
    }

    inner class PaymentHistoryViewHolder(val binding:TemplatePaymentHistoryType2Binding,private val mListener: PaymentHistoryInterface):RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                mListener.onPaymentHistoryClick(paymentsList[adapterPosition])
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


    interface PaymentHistoryInterface{
        fun onPaymentHistoryClick(transactionModel: TransactionModel)
    }

}