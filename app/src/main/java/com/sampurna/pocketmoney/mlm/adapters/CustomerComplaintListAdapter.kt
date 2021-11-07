package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateCustomerCommissionBinding
import com.sampurna.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.sampurna.pocketmoney.utils.convertISOTimeToDateTime

class CustomerComplaintListAdapter(private val customerComplaintAdapterInterface: CustomerComplaintAdapterInterface):RecyclerView.Adapter<CustomerComplaintListAdapter.ComplaintListViewHolder>() {


    private val complaintList = mutableListOf<CustomerComplaintModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintListViewHolder {
        return ComplaintListViewHolder(TemplateCustomerCommissionBinding.inflate(LayoutInflater.from(parent.context),parent,false),customerComplaintAdapterInterface)
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

    inner class ComplaintListViewHolder(
        val binding:TemplateCustomerCommissionBinding,
        val complaintAdapterInterface: CustomerComplaintAdapterInterface
    ):RecyclerView.ViewHolder(binding.root){

        init {
            binding.btnViewComplaint.setOnClickListener {
                complaintAdapterInterface.onViewComplaint(complaintList[absoluteAdapterPosition])
            }
        }
        fun createCoupon(complaintModel:CustomerComplaintModel){

            binding.apply {
                tvComplaintId.text = complaintModel.ComplainID
                tvMessage.text = complaintModel.ResponderComment.toString()
                tvDateStamp.text = convertISOTimeToDateTime(complaintModel.RegisteredOn)
                cpStatus.text = complaintModel.Status
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

    interface CustomerComplaintAdapterInterface{
        fun onViewComplaint(complaintModel: CustomerComplaintModel)
    }

}