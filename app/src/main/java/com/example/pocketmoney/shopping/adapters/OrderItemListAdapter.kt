package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplateOrderListItemWithImgBinding
import com.example.pocketmoney.shopping.model.orderModule.OrderItemModel
import com.example.pocketmoney.shopping.model.orderModule.OrderListModel
import com.example.pocketmoney.utils.Constants

class OrderItemListAdapter:RecyclerView.Adapter<OrderItemListAdapter.OrderItemListViewHolder>() {

    private val orderItems = mutableListOf<OrderItemModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemListViewHolder {
        return OrderItemListViewHolder(TemplateOrderListItemWithImgBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OrderItemListViewHolder, position: Int) {
        holder.createOrderItem(orderItems[position])
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    fun setOrderItemList(orderItems:MutableList<OrderItemModel>){
        this.orderItems.clear()
        this.orderItems.addAll(orderItems)
        notifyDataSetChanged()
    }

    inner class OrderItemListViewHolder(val binding:TemplateOrderListItemWithImgBinding):RecyclerView.ViewHolder(binding.root){

        fun createOrderItem(orderItem:OrderItemModel){
            binding.apply {
                tvProductName.text = orderItem.ProductName
                tvItemPrice.text = "₹ ${orderItem.ProductPrice}"
                tvItemQuantity.text = orderItem.Quantity.toString()
                val imagePath: String =
                        Constants.IMAGE_PATH_PREFIX + orderItem.ImageUrl
                Glide.with(binding.root)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_photo_library)
                        .error(R.drawable.ic_error)
                        .into(imgProductItem)
            }
        }
    }


}