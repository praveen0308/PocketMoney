package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplatePaymentHistoryType2Binding
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutTransaction
import com.example.pocketmoney.utils.convertISOTimeToDateTime

class PayoutTransactionsAdapter(private val mListener: PayoutTransactionsInterface) :
    RecyclerView.Adapter<PayoutTransactionsAdapter.PayoutTransactionsViewHolder>() {


    private val mList = mutableListOf<PayoutTransaction>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PayoutTransactionsViewHolder {
        return PayoutTransactionsViewHolder(
            TemplatePaymentHistoryType2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: PayoutTransactionsViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setPayoutTransactionList(mList: List<PayoutTransaction>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class PayoutTransactionsViewHolder(
        val binding: TemplatePaymentHistoryType2Binding,
        private val mListener: PayoutTransactionsInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onPayoutTransactionClick(mList[absoluteAdapterPosition])
            }


        }

        fun bind(item: PayoutTransaction) {
            binding.apply {
                ivIcon.setImageResource(R.drawable.ic_round_call_made_24)
                tvMainTitle.text = "Paid to"
                tvSecondaryTitle.text = "-₹".plus(item.TransferAmount)
                tvSubTitle.text = item.BeneficiaryName
                tvDate.text = convertISOTimeToDateTime(item.TransferDate!!)

            }
        }
    }

    interface PayoutTransactionsInterface {
        fun onPayoutTransactionClick(transaction: PayoutTransaction)
    }


}