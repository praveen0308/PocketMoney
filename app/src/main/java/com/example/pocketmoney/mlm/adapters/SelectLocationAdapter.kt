package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateSingleListItemBinding
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.example.pocketmoney.shopping.model.ModelState

class SelectLocationAdapter(private val mListener: SelectLocationInterface) :
    RecyclerView.Adapter<SelectLocationAdapter.SelectLocationViewHolder>() {


    private val mList = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectLocationViewHolder {
        return SelectLocationViewHolder(
            TemplateSingleListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: SelectLocationViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setAnyList(mList: List<Any>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class SelectLocationViewHolder(
        val binding: TemplateSingleListItemBinding,
        private val mListener: SelectLocationInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onLocationSelected(mList[absoluteAdapterPosition])
            }

        }

        fun bind(item: Any) {
            binding.apply {
                tvTitle.text = when(item){
                    is ModelOperator->item.name
                    is IdNameModel->item.Name
                    is ModelState-> item.State1
                    else-> ""
                }
            }
        }
    }

    interface SelectLocationInterface {
        fun onLocationSelected(item: Any)
    }


}