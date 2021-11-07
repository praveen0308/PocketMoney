package com.sampurna.pocketmoney.utils.MyCustomNavigationDrawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateTextSubitemBinding
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum

class MyNavigationChildAdapter(
        private val myNavigationChildInterface: MyNavigationChildInterface,
        private val subItemList : List<ModelSubItem>
        ) :RecyclerView.Adapter<MyNavigationChildAdapter.MyNavigationChildViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNavigationChildViewHolder {
        return MyNavigationChildViewHolder(TemplateTextSubitemBinding.
        inflate(LayoutInflater.from(parent.context),parent,false),myNavigationChildInterface)
    }

    override fun onBindViewHolder(holder: MyNavigationChildViewHolder, position: Int) {
        holder.createSubItem(subItemList[position])
    }

    override fun getItemCount(): Int {
        return subItemList.size
    }


    inner class MyNavigationChildViewHolder(val binding:TemplateTextSubitemBinding,private val mListener: MyNavigationChildInterface):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                mListener.onSubItemClick(subItemList[absoluteAdapterPosition].action)
            }
        }
        fun createSubItem(subItem: ModelSubItem){
            binding.apply {
                tvSubitemText.text = subItem.title
            }
        }
    }

    interface MyNavigationChildInterface{
        fun onSubItemClick(action:NavigationEnum)
    }

}