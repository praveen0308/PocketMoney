package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateMyCouponBinding
import com.sampurna.pocketmoney.mlm.model.mlmModels.CouponModel
import com.sampurna.pocketmoney.utils.convertISOTimeToDateTime

class MyCouponsAdapter:RecyclerView.Adapter<MyCouponsAdapter.MyCouponsViewHolder>() {


    private val couponList = mutableListOf<CouponModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCouponsViewHolder {
        return MyCouponsViewHolder(TemplateMyCouponBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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

    inner class MyCouponsViewHolder(val binding:TemplateMyCouponBinding):RecyclerView.ViewHolder(binding.root){

        fun createCoupon(coupon:CouponModel){

            binding.apply {
                tvCouponSrNo.text = coupon.PinSerialNo.toString()
                tvCouponNumber.text = coupon.PinNo.toString()
                tvCouponDate.text = convertISOTimeToDateTime(coupon.GeneratedOn)
                tvCouponStatus.text = if (coupon.PinStatus == "Used"){
                    tvCouponStatus.setBackgroundColor(ContextCompat.getColor(tvCouponStatus.context, R.color.Green))
//                    tvCouponStatus.setTextColor(ContextCompat.getColor(tvCouponStatus.context, R.color.Green))
                    coupon.PinStatus
                }
                else
                {
                    tvCouponStatus.setBackgroundColor(ContextCompat.getColor(tvCouponStatus.context, R.color.Green))
//                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Red))
                    coupon.PinStatus
                }


            }

        }

    }

}