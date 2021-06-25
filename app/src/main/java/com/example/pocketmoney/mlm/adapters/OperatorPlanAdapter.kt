package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateMobileOperatorSimplePlanBinding
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.SpecialPlan

class OperatorPlanAdapter(
    private val planList: ArrayList<MobileOperatorPlan>,
    private val operatorPlanAdapterListener: OperatorPlanAdapterListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1->SimplePlanViewHolder(TemplateMobileOperatorSimplePlanBinding.
                inflate(LayoutInflater.from(parent.context),parent,false),operatorPlanAdapterListener)
            2->SpecialPlanViewHolder(TemplateMobileOperatorSimplePlanBinding.
            inflate(LayoutInflater.from(parent.context),parent,false))
            else->SimplePlanViewHolder(TemplateMobileOperatorSimplePlanBinding.
            inflate(LayoutInflater.from(parent.context),parent,false),operatorPlanAdapterListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(getItemViewType(position)){
//            1->
//            2->SpecialPlanViewHolder(TemplateMobileOperatorSimplePlanBinding.
//            inflate(LayoutInflater.from(parent.context),parent,false))
//            else->(holder as SimplePlanViewHolder).createPlanItem(planList[position])
//        }
//        holder.createPlanItem(planList[position])
        (holder as SimplePlanViewHolder).createPlanItem(planList[position])
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(planList[position].javaClass.simpleName){
            MobileOperatorPlan::class.java.simpleName->1
            SpecialPlan::class.java.simpleName->2
            else->0
        }
    }
//    fun setContactList(planList : ArrayList<ModelPlan>){
//        this.planList.clear()
//        this.planList.addAll(planList)
//        notifyDataSetChanged()
//    }

    inner class SimplePlanViewHolder(val binding:TemplateMobileOperatorSimplePlanBinding,private val mListener:OperatorPlanAdapterListener):RecyclerView.ViewHolder(binding.root){

        init {
            binding.btnAmount.setOnClickListener {
                mListener.onPlanClick(planList[absoluteAdapterPosition])
            }
        }

        fun createPlanItem(modelPlan: MobileOperatorPlan){
            binding.apply {

                txtPlanValidity.text=modelPlan.validity
                txtPlanDescription.text=modelPlan.desc
                btnAmount.text="₹${modelPlan.rs}"


            }
        }
    }

    inner class SpecialPlanViewHolder(val binding:TemplateMobileOperatorSimplePlanBinding):RecyclerView.ViewHolder(binding.root){

        fun createPlanItem(modelPlan: SpecialPlan){
            binding.apply {

                txtPlanValidity.text="-"
                txtPlanDescription.text=modelPlan.desc
                btnAmount.text="₹${modelPlan.rs}"

            }
        }
    }

    interface OperatorPlanAdapterListener{
        fun onPlanClick(plan:MobileOperatorPlan)
    }

}