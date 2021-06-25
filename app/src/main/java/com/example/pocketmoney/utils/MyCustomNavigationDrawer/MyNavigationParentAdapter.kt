package com.example.pocketmoney.utils.MyCustomNavigationDrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplateTextSubitemBinding
import com.example.pocketmoney.databinding.TemplateTextWithExpandingIconBinding
import com.example.pocketmoney.utils.myEnums.NavigationEnum

class MyNavigationParentAdapter(private val myNavigationChildInterface: MyNavigationChildAdapter.MyNavigationChildInterface,
                                private val myNavigationParentAdapterInterface:MyNavigationParentAdapterInterface) : RecyclerView.Adapter<MyNavigationParentAdapter.MyNavigationParentViewHolder>() {

    private val itemList = mutableListOf<ModelItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNavigationParentViewHolder {
        return MyNavigationParentViewHolder(TemplateTextWithExpandingIconBinding.inflate(LayoutInflater.from(parent.context), parent, false),myNavigationParentAdapterInterface)
    }

    override fun onBindViewHolder(holder: MyNavigationParentViewHolder, position: Int) {
        holder.createSubItem(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setNavigationItemList(itemList: List<ModelItem>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyNavigationParentViewHolder(val binding: TemplateTextWithExpandingIconBinding,private val mListener:MyNavigationParentAdapterInterface) : RecyclerView.ViewHolder(binding.root) {
        init {

            itemView.setOnClickListener {
                for (i in 0 until itemList.size) {
                    if (i == absoluteAdapterPosition) {
                        itemList[i].isExpanded = itemList[i].isExpanded != true

                    } else {
                        itemList[i].isExpanded = false
                    }
                }
                mListener.onParentNavItemClick(itemList[absoluteAdapterPosition].action)
                notifyDataSetChanged()
            }
        }

        fun createSubItem(item: ModelItem) {
            binding.apply {
                tvItemText.text = item.title
                rvSubItems.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = MyNavigationChildAdapter(myNavigationChildInterface, item.subItemList)
                }
                if (item.subItemList.isEmpty()) {
                    imgOpeningIndicator.visibility = View.GONE
                } else {
                    imgOpeningIndicator.visibility = View.VISIBLE
                    if (item.isExpanded) {
                        imgOpeningIndicator.setImageResource(R.drawable.icon_minus)
                        rvSubItems.visibility = View.VISIBLE
                    } else {
                        imgOpeningIndicator.setImageResource(R.drawable.icon_add)
                        rvSubItems.visibility = View.GONE
                    }
                }

            }
        }
    }


    interface MyNavigationParentAdapterInterface{

        fun onParentNavItemClick(action:NavigationEnum)
    }

}