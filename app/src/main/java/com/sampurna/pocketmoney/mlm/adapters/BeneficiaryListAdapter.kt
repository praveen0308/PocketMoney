package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateBeneficiaryListItemBinding
import com.sampurna.pocketmoney.mlm.model.payoutmodels.Beneficiary


class BeneficiaryListAdapter(private val mListener: BeneficiaryListInterface) :
    RecyclerView.Adapter<BeneficiaryListAdapter.BeneficiaryListViewHolder>() {


    private val mList = mutableListOf<Beneficiary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryListViewHolder {
        return BeneficiaryListViewHolder(
            TemplateBeneficiaryListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: BeneficiaryListViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setBeneficiaryList(mList: List<Beneficiary>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class BeneficiaryListViewHolder(
        val binding: TemplateBeneficiaryListItemBinding,
        private val mListener: BeneficiaryListInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onBeneficiaryClick(mList[absoluteAdapterPosition])
            }
            binding.btnTransfer.setOnClickListener {
                mListener.onTransferClick(mList[absoluteAdapterPosition])
            }


        }

        fun bind(item: Beneficiary) {
            binding.apply {
                tvName.text = item.BeneficiaryName
                tvAccount.text = item.Account
            }
        }
    }

    interface BeneficiaryListInterface {
        fun onTransferClick(beneficiary: Beneficiary)
        fun onBeneficiaryClick(beneficiary: Beneficiary)
    }


}