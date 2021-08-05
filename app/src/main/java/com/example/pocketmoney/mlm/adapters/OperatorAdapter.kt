package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateOperatorListItemBinding
import com.example.pocketmoney.databinding.TemplateRechargeComponentBinding
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.ModelRechargeComponent

class OperatorAdapter(private val operatorAdapterInterface: OperatorAdapterInterface) : RecyclerView.Adapter<OperatorAdapter.OperatorViewHolder>() {

    private val operatorList: MutableList<ModelOperator> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorViewHolder {
        return OperatorViewHolder(TemplateOperatorListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), operatorAdapterInterface)
    }

    override fun onBindViewHolder(holder: OperatorViewHolder, position: Int) {
        holder.bindOperator(operator = operatorList[position])
    }

    override fun getItemCount(): Int {
        return operatorList.size
    }


    fun setComponentList(operatorList: List<ModelOperator>) {
        this.operatorList.clear()
        this.operatorList.addAll(operatorList)
        notifyDataSetChanged()

    }

    inner class OperatorViewHolder(val binding: TemplateOperatorListItemBinding, private val operatorAdapterInterface: OperatorAdapterInterface) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                operatorAdapterInterface.onOperatorClick(operatorList[absoluteAdapterPosition])
            }
        }

        fun bindOperator(operator: ModelOperator) {
            binding.apply {
                tvOperatorName.text = operator.name
                ivOperatorLogo.setImageResource(operator.imageUrl as Int)
            }
        }
    }


    public interface OperatorAdapterInterface {
        fun onOperatorClick(operator: ModelOperator)
    }


}