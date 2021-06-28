package com.example.pocketmoney.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.LayoutOrderAmountSummaryBinding


class OrderAmountSummary @kotlin.jvm.JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutOrderAmountSummaryBinding = LayoutOrderAmountSummaryBinding.inflate(LayoutInflater.from(context))

    init {

        addView(binding.root)
//        inflate(context, R.layout.layout_order_amount_summary,this)

    }

    fun setAmountSummary(orderAmountSummary: ModelOrderAmountSummary){

        binding.apply {
            tvNoOfItems.text = "(".plus(orderAmountSummary.itemQuantity.toString()).plus(")")
            tvProductOldPrice.text = "₹ ".plus(orderAmountSummary.productOldPrice.toString())
            tvTotalSaving.text = "-₹ ".plus(orderAmountSummary.saving.toString())
            tvTotalAmount.text = "₹ ".plus(orderAmountSummary.totalPrice.toString())

            tvDeliveryCharges.text = "+₹ ".plus(orderAmountSummary.shippingCharge.toString())
            tvExtraDiscount.text = "-₹ ".plus(orderAmountSummary.extraDiscount.toString())
            tvTax.text = "+₹ ".plus(orderAmountSummary.tax.toString())
            tvAmountPayable.text = "₹ ".plus(orderAmountSummary.grandTotal.toString())
        }
    }

    fun setVisibilityStatus(status: Int){
        when(status){
            1 -> binding.clGrandTotal.visibility = VISIBLE
            else-> binding.clGrandTotal.visibility= GONE
        }
    }

    fun observePriceDetails(){
        val shake: Animation = AnimationUtils.loadAnimation(context, R.anim.shake)
        binding.lblPriceDetails.startAnimation(shake) // starts animation

    }
}