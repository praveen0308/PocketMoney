package com.jmm.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateOrderListItemNormalBinding
import com.jmm.core.utils.convertISOTimeToDate
import com.jmm.model.myEnums.OrderStatus
import com.jmm.model.shopping_models.OrderListItem

class OrderListAdapter(private val orderListAdapterInterface: OrderListAdapterInterface):RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>(){

    private val orderItemList = mutableListOf<OrderListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(
            TemplateOrderListItemNormalBinding.
        inflate(LayoutInflater.from(parent.context),parent,false),
                orderListAdapterInterface)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.createOrderListItem(orderItemList[position])
    }

    override fun getItemCount(): Int {
        return orderItemList.size
    }

    fun setOrderList(orderItemList:MutableList<OrderListItem>){
        this.orderItemList.clear()
        this.orderItemList.addAll(orderItemList)
        notifyDataSetChanged()

    }

    inner class OrderListViewHolder(val binding:TemplateOrderListItemNormalBinding,private val mListener: OrderListAdapterInterface):RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnOrderDetails.setOnClickListener {
                mListener.onOrderDetailClick(orderItemList[adapterPosition])
            }
        }

        fun createOrderListItem(item: OrderListItem){
            binding.apply {
                tvOrderNumber.text = item.OrderNumber
                tvTotalAmount.text = "₹ ${item.GrandTotal}"
                tvOrderDate.text = convertISOTimeToDate(item.OrderDate)
                tvOrderStatus.text = OrderStatus.getStatus(item.OrderStatus).toString()
            }
        }
    }

    interface OrderListAdapterInterface{
        fun onOrderDetailClick(item: OrderListItem)
    }
}