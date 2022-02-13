package com.jmm.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jmm.core.databinding.TemplateOrderListItemWithImgBinding
import com.jmm.core.utils.Constants
import com.jmm.model.shopping_models.orderModule.OrderItemModel
import com.jmm.shopping.R

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

        fun createOrderItem(orderItem: OrderItemModel){
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