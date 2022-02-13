package com.jmm.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateSectionedRvBinding
import com.jmm.model.ModelParentMenu

class AccountSettingParentAdapter(
    private val menuList: List<ModelParentMenu>,
    private val accountSettingChildInterface: AccountSettingChildAdapter.AccountSettingChildInterface
)
    :RecyclerView.Adapter<AccountSettingParentAdapter.AccountSettingParentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSettingParentViewHolder {
        return AccountSettingParentViewHolder(
            TemplateSectionedRvBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: AccountSettingParentViewHolder, position: Int) {
        holder.createMenus(menuList[position])
    }

    override fun getItemCount(): Int {
        return menuList.size
    }


    inner class AccountSettingParentViewHolder(val binding: TemplateSectionedRvBinding):RecyclerView.ViewHolder(binding.root){

        fun createMenus(menu: ModelParentMenu){
            binding.apply {
                templateSectionedRvTitle.text = menu.title
                templateSectionedRvRecyclerview.apply {
                    setHasFixedSize(true)
                    val mLayoutManager = LinearLayoutManager(context)
                    val dividerItemDecoration = DividerItemDecoration(context,
                            mLayoutManager.orientation)
                    addItemDecoration(dividerItemDecoration)
                    layoutManager = mLayoutManager
                    adapter = AccountSettingChildAdapter(menu.menuItemList,accountSettingChildInterface)
                }
            }

        }

    }

}