package com.example.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateAddressListItemNewBinding
import com.example.pocketmoney.shopping.model.ModelAddress

class SelectAddressAdapter(private val mListener: SelectAddressInterface) :
    RecyclerView.Adapter<SelectAddressAdapter.SelectAddressViewHolder>() {


    private val mList = mutableListOf<ModelAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAddressViewHolder {
        return SelectAddressViewHolder(
            TemplateAddressListItemNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: SelectAddressViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setCustomerAddressList(mList: List<ModelAddress>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class SelectAddressViewHolder(
        val binding: TemplateAddressListItemNewBinding,
        private val mListener: SelectAddressInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                for (i in 0 until mList.size){
                    mList[i].isSelected = false
                }
                mList[absoluteAdapterPosition].isSelected= true
                mListener.onAddressSelect(mList[absoluteAdapterPosition])
                notifyDataSetChanged()

            }

            binding.btnEdit.setOnClickListener {
                mListener.onEditClick(mList[absoluteAdapterPosition])
            }


            binding.btnRemove.setOnClickListener {
                mListener.onRemoveClick(mList[absoluteAdapterPosition])
            }
        }

        fun bind(item: ModelAddress) {
            binding.apply {
                radioButton.text = item.Name
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

                radioButton.isChecked = item.isSelected == true

            }
        }
    }


    interface SelectAddressInterface {
        fun onAddressSelect(item: ModelAddress)
        fun onEditClick(item: ModelAddress)
        fun onRemoveClick(item: ModelAddress)
    }
}