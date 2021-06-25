package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplatePaymentMethodBinding
import com.example.pocketmoney.databinding.TemplateSavedPaymentMethodBinding
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.shopping.model.ModelMasterPaymentMethod
import com.example.pocketmoney.shopping.model.ModelPaymentCard
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import java.util.ArrayList


class PaymentMethodAdapter(private val methodList:MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.template_saved_payment_method->SavedCardViewHolder(
                TemplateSavedPaymentMethodBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else->OtherPaymentMethodViewHolder(
                TemplatePaymentMethodBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            R.layout.template_saved_payment_method->(holder as SavedCardViewHolder).createSavedCard(methodList[position] as ModelPaymentCard)
            else->(holder as OtherPaymentMethodViewHolder).createPaymentMethod(methodList[position] as ModelPaymentMethod)
        }
    }

    override fun getItemCount(): Int {
        return methodList.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (methodList[position].javaClass.name.equals(ModelPaymentCard::class.simpleName)) {
            R.layout.template_saved_payment_method
        } else{
            R.layout.template_payment_method
        }
    }


    inner class SavedCardViewHolder(val binding:TemplateSavedPaymentMethodBinding):RecyclerView.ViewHolder(binding.root){
        fun createSavedCard(card:ModelPaymentCard){
            binding.apply {
                tvCardNumber.text="**** **** **** "+card.cardNumber.takeLast(4)
                tvCardExpiry.text="Expiring "+card.expiryMonth.toString()+"/"+card.expiryYear.toString()

            }

        }
    }

    inner class OtherPaymentMethodViewHolder(val binding:TemplatePaymentMethodBinding):RecyclerView.ViewHolder(binding.root){

        init {

            itemView.setOnClickListener {
                for (i in 0 until methodList.size){
                    val method = methodList[i] as ModelPaymentMethod
                    method.isSelected = i==absoluteAdapterPosition
                }
                notifyDataSetChanged()
            }
        }

        fun createPaymentMethod(method:ModelPaymentMethod){

            binding.apply {
                tvPaymentMethodName.text = method.methodName
                if (method.isSelected==true){
                    rbIndicator.isChecked = true
                }
                else{
                    rbIndicator.isChecked = false
                }
            }
        }
    }
}