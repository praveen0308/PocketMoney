package com.sampurna.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplatePaymentCardViewBinding
import com.sampurna.pocketmoney.shopping.model.ModelPaymentCard


class SavedPaymentCardAdapter() : RecyclerView.Adapter<SavedPaymentCardAdapter.SavedCardViewHolder>(){

    private val cardList= mutableListOf<ModelPaymentCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPaymentCardAdapter.SavedCardViewHolder {
        return SavedCardViewHolder(
            TemplatePaymentCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.createSavedCard(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setCardList(cardList:MutableList<ModelPaymentCard>){
        this.cardList.clear()
        this.cardList.addAll(cardList)
        notifyDataSetChanged()
    }

    inner class SavedCardViewHolder(val binding:TemplatePaymentCardViewBinding):RecyclerView.ViewHolder(binding.root){
        fun createSavedCard(card:ModelPaymentCard){
            binding.apply {
                tvCardNumber.text="**** **** **** "+card.cardNumber.takeLast(4)
                tvCardHolderName.text = card.cardHolderName
                tvCardExpiryDate.text=card.expiryMonth.toString()+"/"+card.expiryYear.toString()

            }
        }
    }



}