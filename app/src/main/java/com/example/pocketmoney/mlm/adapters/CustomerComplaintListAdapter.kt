package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateCustomerCommissionBinding
import com.example.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.example.pocketmoney.utils.convertISOTimeToDateTime

class CustomerComplaintListAdapter:RecyclerView.Adapter<CustomerComplaintListAdapter.ComplaintListViewHolder>() {


    private val complaintList = mutableListOf<CustomerComplaintModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintListViewHolder {
        return ComplaintListViewHolder(TemplateCustomerCommissionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ComplaintListViewHolder, position: Int) {
        holder.createCoupon(complaintList[position])
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    fun setComplaintList(complaintList:List<CustomerComplaintModel>){
        this.complaintList.clear()
        this.complaintList.addAll(complaintList)
        notifyDataSetChanged()
    }

    inner class ComplaintListViewHolder(val binding:TemplateCustomerCommissionBinding):RecyclerView.ViewHolder(binding.root){

        fun createCoupon(complaintModel:CustomerComplaintModel){

            binding.apply {
                tvReferenceId.text = complaintModel.ComplainID
                tvMainTitle.text = complaintModel.RespondedBy.toString()
                tvSubTitle.text = convertISOTimeToDateTime(complaintModel.RegisteredOn)
//                tvAmount.text = if (coupon.PinStatus == "Used"){
//                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Green))
//                    coupon.PinStatus
//                }
//                else
//                {
//                    tvAmount.setTextColor(ContextCompat.getColor(tvAmount.context, R.color.Red))
//                    coupon.PinStatus
//                }


            }

        }

    }

}