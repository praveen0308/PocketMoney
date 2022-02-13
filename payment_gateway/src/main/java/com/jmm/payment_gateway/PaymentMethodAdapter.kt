package com.jmm.payment_gateway

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.model.shopping_models.ModelPaymentCard
import com.jmm.model.shopping_models.ModelPaymentMethod
import com.jmm.payment_gateway.databinding.TemplatePaymentMethodBinding
import com.jmm.payment_gateway.databinding.TemplateSavedPaymentMethodBinding


class PaymentMethodAdapter(private val methodList:MutableList<Any>,
private val paymentMethodInterface: PaymentMethodInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.template_saved_payment_method->SavedCardViewHolder(
                TemplateSavedPaymentMethodBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else->OtherPaymentMethodViewHolder(
                TemplatePaymentMethodBinding.inflate(LayoutInflater.from(parent.context),parent,false),paymentMethodInterface)
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


    inner class SavedCardViewHolder(val binding: TemplateSavedPaymentMethodBinding):RecyclerView.ViewHolder(binding.root){
        fun createSavedCard(card: ModelPaymentCard){
            binding.apply {
                tvCardNumber.text="**** **** **** "+card.cardNumber.takeLast(4)
                tvCardExpiry.text="Expiring "+card.expiryMonth.toString()+"/"+card.expiryYear.toString()

            }

        }
    }

    inner class OtherPaymentMethodViewHolder(
        val binding: TemplatePaymentMethodBinding,
        val mListener: PaymentMethodInterface
    )
        :RecyclerView.ViewHolder(binding.root){

        init {

            itemView.setOnClickListener {
                for (i in 0 until methodList.size){
                    val method = methodList[i] as ModelPaymentMethod
                    method.isSelected = i==adapterPosition
                }
                mListener.onPaymentModeSelected(methodList[adapterPosition] as ModelPaymentMethod)
                notifyDataSetChanged()
            }
        }

        fun createPaymentMethod(method: ModelPaymentMethod){

            binding.apply {
//                tvPaymentMethodName.text = method.methodName
                rbIndicator.text = method.methodName
                rbIndicator.isChecked = method.isSelected==true
                tvPaymentMethodImage.setImageResource(method.imageUrl)
            }
        }
    }

    interface PaymentMethodInterface{
        fun onPaymentModeSelected(item: ModelPaymentMethod)
    }
}