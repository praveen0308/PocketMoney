package com.jmm.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateAddressListItemBinding
import com.jmm.core.utils.MyAddressView
import com.jmm.model.shopping_models.ModelAddress

class AddressAdapter(private val mListener: AddressAdapterListener) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

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

    fun setAddressList(addressList: List<ModelAddress>) {
        this.addressList.clear()
        this.addressList.addAll(addressList)
        notifyDataSetChanged()
    }


    inner class AddressViewHolder(
            val binding: TemplateAddressListItemBinding,
            private val addressAdapterListener: AddressAdapterListener)
        : RecyclerView.ViewHolder(binding.root), MyAddressView.MyAddressViewInterface {

        init {
            binding.btnEdit.setOnClickListener {
                addressAdapterListener.onEditButtonClick(addressList[absoluteAdapterPosition])
            }

            binding.btnRemove.setOnClickListener {
                addressAdapterListener.onRemoveButtonClick(addressList[absoluteAdapterPosition])
            }

        }

        fun createAddressView(item: ModelAddress) {

            binding.apply {
                tvName.text = item.Name
                val sbAddress = StringBuilder()
                item.AddressType?.let {
                    cpAddressType.isVisible = true
                    cpAddressType.text = it
                }
                sbAddress.append(item.Address1).append(", ")
                sbAddress.append(item.Street).append(", ")
                sbAddress.append(item.Locality).append(", ")
                sbAddress.append(item.CityName).append(" - ")
                sbAddress.append(item.PostalCode).append(", ")
                sbAddress.append(item.StateName).append(", ")
                sbAddress.append(item.CountryName)

                tvAddress.text = sbAddress.toString()

                tvMobileNumber.text = item.MobileNo



            }
        }

        override fun onActionButtonClick() {
            addressAdapterListener.onActionButtonClick(addressList[absoluteAdapterPosition])
        }
    }

    interface AddressAdapterListener {
        fun onActionButtonClick(modelAddress: ModelAddress)
        fun onEditButtonClick(modelAddress: ModelAddress)
        fun onRemoveButtonClick(modelAddress: ModelAddress)
    }


}