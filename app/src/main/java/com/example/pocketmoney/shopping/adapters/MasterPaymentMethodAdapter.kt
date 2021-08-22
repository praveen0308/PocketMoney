package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateSectionedRvBinding
import com.example.pocketmoney.shopping.model.ModelMasterPaymentMethod

class MasterPaymentMethodAdapter: RecyclerView.Adapter<MasterPaymentMethodAdapter.MasterPaymentMethodViewHolder>() {

    private val paymentCategoryList = mutableListOf<ModelMasterPaymentMethod>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterPaymentMethodViewHolder {
        return MasterPaymentMethodViewHolder(TemplateSectionedRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MasterPaymentMethodViewHolder, position: Int) {
        holder.createPaymentMethodCategory(paymentCategoryList[position])
    }

    override fun getItemCount(): Int {
        return paymentCategoryList.size
    }

    fun setPaymentCategoryList(mList : List<ModelMasterPaymentMethod>){
        paymentCategoryList.clear()
        paymentCategoryList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class MasterPaymentMethodViewHolder(val binding:TemplateSectionedRvBinding):RecyclerView.ViewHolder(binding.root){

        fun createPaymentMethodCategory(cat:ModelMasterPaymentMethod){
            binding.templateSectionedRvTitle.text = cat.title
            binding.templateSectionedRvRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context,2)
//                layoutManager = LinearLayoutManager(context)
//                adapter = PaymentMethodAdapter(cat.methodList)
            }
        }
    }

}