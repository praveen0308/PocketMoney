package com.jmm.mlm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateHomeServicesBinding
import com.jmm.model.ModelServiceView
import com.jmm.model.myEnums.MyEnums

class ServiceViewAdapter(private var serviceList:List<ModelServiceView>, val mListener: com.jmm.core.HomeParentItemListener) : RecyclerView.Adapter<ServiceViewAdapter.ServiceViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewViewHolder {
        val binding = TemplateHomeServicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ServiceViewViewHolder(binding,mListener)
    }

    override fun onBindViewHolder(holder: ServiceViewViewHolder, position: Int) {
        holder.bindServiceView(serviceList[position])
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    inner class ServiceViewViewHolder(val binding: TemplateHomeServicesBinding, private val mListener: com.jmm.core.HomeParentItemListener):RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener(View.OnClickListener {
                mListener.onItemClick(MyEnums.SERVICES, serviceList[adapterPosition].action)
            })
        }

        fun bindServiceView(serviceView: ModelServiceView){
            binding.templateHomeServicesTitle.text = serviceView.serviceTitle
            binding.templateHomeServicesImg.setImageResource(serviceView.imageUrl)
        }
    }
}