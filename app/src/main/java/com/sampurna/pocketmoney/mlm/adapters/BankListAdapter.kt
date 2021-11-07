package com.sampurna.pocketmoney.mlm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateContactListItemBinding
import com.sampurna.pocketmoney.mlm.model.payoutmodels.BankModel
import java.util.*
import kotlin.collections.ArrayList

class BankListAdapter(private val sBankListener: BankAdapterInterface): RecyclerView.Adapter<BankListAdapter.BankViewHolder>() {

    private var bankList = ArrayList<BankModel>()
    private var bankListFiltered : MutableList<BankModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        return BankViewHolder(TemplateContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),sBankListener)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        holder.createBank(bankListFiltered[position])
    }

    override fun getItemCount(): Int {
        return bankListFiltered.size
    }

    fun setBankList(bankList : List<BankModel>){
        this.bankList.clear()
        this.bankList.addAll(bankList)
        notifyDataSetChanged()
    }
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    bankListFiltered = bankList
                } else {

                    val filteredList: MutableList<BankModel> = ArrayList()
                    for (row in bankList) {
                        val strRegex = Regex(pattern = "[a-zA-Z]+")
                        val numberRegex = Regex(pattern = "[0-9]+")
                        if (charSequence.matches(strRegex)){
                            if (row.BankName!!.toLowerCase()
                                    .contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }else if (charSequence.matches(numberRegex)){
                            if (row.IFSC.toString().contains(charSequence)) {
                                filteredList.add(row)
                            }
                        }

                    }
                    bankListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = bankListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                bankListFiltered = filterResults.values as ArrayList<BankModel>
                notifyDataSetChanged()
            }
        }
    }
    inner class BankViewHolder(val binding: TemplateContactListItemBinding, private val bankListener:BankAdapterInterface) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                bankListener.onBankClick(bankListFiltered[absoluteAdapterPosition])
            }
        }
        fun createBank(bank: BankModel){
            binding.apply {

                tvName.text = bank.BankName
//                tvNumber.text = bank.bankNumber?.let { extractMobileNumber(it) }
                tvNumber.text = bank.IFSC.toString()
                tvSymbol.text = bank.BankName!!.take(1)

                val rnd = Random()
                val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                cdSymbolBackground.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
            }
        }

    }

    interface BankAdapterInterface{
        fun onBankClick(bank:BankModel)
    }

}