package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateCustomMenuItemBinding
import com.example.pocketmoney.shopping.model.ModelCustomMenuItem
import com.example.pocketmoney.utils.myEnums.MenuEnum

class CustomMenuAdapter(private val mCustomMenuListener: CustomMenuListener):RecyclerView.Adapter<CustomMenuAdapter.CustomMenuViewHolder>() {

    private val menuList = mutableListOf<ModelCustomMenuItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomMenuViewHolder {
        return CustomMenuViewHolder(TemplateCustomMenuItemBinding.inflate(LayoutInflater.from(parent.context)
            ,parent,false),mCustomMenuListener)
    }

    override fun onBindViewHolder(holder: CustomMenuViewHolder, position: Int) {
        holder.createMenuItem(menuList[position])
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    fun setMenuList(menuList:MutableList<ModelCustomMenuItem>){
        this.menuList.clear()
        this.menuList.addAll(menuList)
        notifyDataSetChanged()
    }

    inner class CustomMenuViewHolder(val binding:TemplateCustomMenuItemBinding,private val customMenuListener: CustomMenuListener):RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                customMenuListener.onMenuItemClick(menuList[absoluteAdapterPosition].action)
            }
        }
        fun createMenuItem(menuItem: ModelCustomMenuItem){
            binding.apply {
                tvTitle.text = menuItem.title
                tvSubtitle.text = menuItem.subtitle
            }

        }
    }


    interface CustomMenuListener{
        fun onMenuItemClick(action: MenuEnum)
    }

}