package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateHomeServicesBinding
import com.sampurna.pocketmoney.mlm.HomeParentItemListener
import com.sampurna.pocketmoney.mlm.model.ModelServiceView
import com.sampurna.pocketmoney.utils.myEnums.MyEnums

class ServiceViewAdapter(private var serviceList:List<ModelServiceView>,val mListener: HomeParentItemListener) : RecyclerView.Adapter<ServiceViewAdapter.ServiceViewViewHolder>() {

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

    inner class ServiceViewViewHolder(val binding: TemplateHomeServicesBinding, private val mListener:HomeParentItemListener):RecyclerView.ViewHolder(binding.root){

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