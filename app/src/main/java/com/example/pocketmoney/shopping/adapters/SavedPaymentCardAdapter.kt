package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplatePaymentCardViewBinding
import com.example.pocketmoney.databinding.TemplatePaymentMethodBinding
import com.example.pocketmoney.databinding.TemplateSavedPaymentMethodBinding
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.shopping.model.ModelMasterPaymentMethod
import com.example.pocketmoney.shopping.model.ModelPaymentCard
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import java.util.ArrayList


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