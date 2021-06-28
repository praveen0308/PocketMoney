package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateMenuItemBinding
import com.example.pocketmoney.mlm.model.ModelMenuItem

class AccountSettingChildAdapter(
    private val menuList:List<ModelMenuItem>,
    private val accountSettingChildInterface: AccountSettingChildInterface
)
    :RecyclerView.Adapter<AccountSettingChildAdapter.AccountSettingChildViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSettingChildViewHolder {
        return AccountSettingChildViewHolder(
            TemplateMenuItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
        ),accountSettingChildInterface)
    }

    override fun onBindViewHolder(holder: AccountSettingChildViewHolder, position: Int) {
        holder.createMenuItem(menuList[position])
    }

    override fun getItemCount(): Int {
        return menuList.size
    }


    inner class AccountSettingChildViewHolder(val binding:TemplateMenuItemBinding,private val mListener: AccountSettingChildInterface):RecyclerView.ViewHolder(binding.root){

        init {

            itemView.setOnClickListener {
                mListener.onMenuClick(menuList[absoluteAdapterPosition])
            }
        }
        fun createMenuItem(menu: ModelMenuItem){
            binding.apply {
                tvMenuTitle.text = menu.title
                if (menu.description.isNullOrEmpty()){
                    tvMenuSubtitle.visibility = View.GONE
                }
                else{
                    tvMenuSubtitle.visibility = View.VISIBLE
                    tvMenuSubtitle.text = menu.description
                }
                ivMenuIcon.setImageResource(menu.iconImage)
            }

        }

    }

    interface AccountSettingChildInterface{
        fun onMenuClick(menu: ModelMenuItem)

    }
}