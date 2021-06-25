package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplateCustomerCommissionBinding
import com.example.pocketmoney.mlm.model.mlmModels.CouponModel
import com.example.pocketmoney.utils.convertISOTimeToDateTime

class MyCouponsAdapter:RecyclerView.Adapter<MyCouponsAdapter.MyCouponsViewHolder>() {


    private val couponList = mutableListOf<CouponModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCouponsViewHolder {
        return MyCouponsViewHolder(TemplateCustomerCommissionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyCouponsViewHolder, position: Int) {
        holder.createCoupon(couponList[position])
    }

    override fun getItemCount(): Int {
        return couponList.size
    }

    fun setCouponList(couponList:List<CouponModel>){
        this.couponList.clear()
        this.couponList.addAll(couponList)
        notifyDataSetChanged()
    }

    inner class MyCouponsViewHolder(val binding:TemplateCustomerCommissionBinding):RecyclerView.ViewHolder(binding.root){

        fun createCoupon(coupon:CouponModel){

            binding.apply {
                tvReferenceId.text = coupon.PinSerialNo.toString()
                tvMainTitle.text = coupon.PinNo.toString()
                tvSubTitle.text = convertISOTimeToDateTime(coupon.GeneratedOn)
                tvAmount.text = if (coupon.PinStatus == "Used"){
                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Green))
                    coupon.PinStatus
                }
                else
                {
                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Red))
                    coupon.PinStatus
                }


            }

        }

    }

}