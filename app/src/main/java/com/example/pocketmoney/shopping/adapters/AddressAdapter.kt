package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateAddressListItemBinding
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.utils.MyAddressView
import com.example.pocketmoney.utils.myEnums.ShoppingEnum

class AddressAdapter(private val source: ShoppingEnum, private val mListener: AddressAdapterListener) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val addressList = mutableListOf<ModelAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(TemplateAddressListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), mListener)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.createAddressView(addressList[position])
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun setAddressList(addressList: MutableList<ModelAddress>) {
        this.addressList.clear()
        this.addressList.addAll(addressList)
        notifyDataSetChanged()
    }


    inner class AddressViewHolder(
            val binding: TemplateAddressListItemBinding,
            private val addressAdapterListener: AddressAdapterListener)
        : RecyclerView.ViewHolder(binding.root), MyAddressView.MyAddressViewInterface {

        init {

            itemView.setOnClickListener {
                for (i in 0 until addressList.size){
                    addressList[i].isSelected = i==absoluteAdapterPosition
                }
                notifyDataSetChanged()
            }
//            when (source) {
//                ShoppingEnum.CHECKOUT -> {
//                    binding.btnDeliverHere.visibility = View.VISIBLE
//                    binding.btnDeliverHere.text = itemView.context.getString(R.string.deliver_here)
//                }
//                ShoppingEnum.MY_ACCOUNT -> {
//                    binding.btnDeliverHere.visibility = View.GONE
//                }
//            }
//
            binding.addressView.setMyAddressViewListener(this)
        }

        fun createAddressView(modelAddress: ModelAddress) {

            binding.apply {
                addressView.setModelAddress(modelAddress)

            }
        }

        override fun onActionButtonClick() {
            addressAdapterListener.onActionButtonClick(addressList[absoluteAdapterPosition])
        }
    }

    interface AddressAdapterListener {
        fun onActionButtonClick(modelAddress: ModelAddress)
        fun onEditButtonClick(modelAddress: ModelAddress)
    }


}