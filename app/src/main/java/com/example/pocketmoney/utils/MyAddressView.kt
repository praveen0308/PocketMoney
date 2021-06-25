package com.example.pocketmoney.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.LayoutAddressViewBinding
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.model.orderModule.CustomerAddress
import com.example.pocketmoney.shopping.model.orderModule.ShippingDetailAddress
import java.lang.StringBuilder

class MyAddressView @kotlin.jvm.JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding = LayoutAddressViewBinding.inflate(LayoutInflater.from(context))
    private var myAddressViewInterface: MyAddressViewInterface?=null

    init {
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyAddressView)

        when(attributes.getInt(R.styleable.MyAddressView_mav_type,0)){
            0 -> {
                binding.imgMenuOption.visibility = View.VISIBLE
            }
            1 ->{
                binding.imgMenuOption.visibility=View.GONE
            }
        }
        attributes.recycle()
    }

    fun setModelAddress(modelAddress: ModelAddress) {
        binding.apply {
            tvName.text = modelAddress.Name
            val sbAddress = StringBuilder()

            sbAddress.append(modelAddress.Address1).append(", ")
            sbAddress.append(modelAddress.Street).append(", ")
            sbAddress.append(modelAddress.Locality).append(", ")
            sbAddress.append(modelAddress.CityName).append(" - ")
            sbAddress.append(modelAddress.PostalCode).append(", ")
            sbAddress.append(modelAddress.StateName).append(", ")
            sbAddress.append(modelAddress.CountryName)

            tvAddress.text = sbAddress.toString()

            tvMobileNumber.text = modelAddress.MobileNo

            if (modelAddress.isSelected == true) {
                cbIndicator.isChecked = true
                cbIndicator.visibility = View.VISIBLE
                btnDeliverHere.visibility = View.VISIBLE
            } else {
                btnDeliverHere.visibility = View.GONE
                cbIndicator.visibility = View.GONE
                cbIndicator.isChecked = false
            }

        }

        binding.btnDeliverHere.setOnClickListener {
            myAddressViewInterface?.onActionButtonClick()
        }

    }

    fun setModelAddress(modelAddress: ShippingDetailAddress) {
        binding.apply {
            tvName.text = modelAddress.Name
            val sbAddress = StringBuilder()

            sbAddress.append(modelAddress.Address1).append(", ")
            sbAddress.append(modelAddress.Street).append(", ")
            sbAddress.append(modelAddress.Locality).append(", ")
            sbAddress.append(modelAddress.CityName).append(" - ")
            sbAddress.append(modelAddress.PostalCode).append(", ")
            sbAddress.append(modelAddress.StateName).append(", ")
            sbAddress.append(modelAddress.CountryName)

            tvAddress.text = sbAddress.toString()

            tvMobileNumber.text = modelAddress.MobileNo

        }

        binding.btnDeliverHere.setOnClickListener {
            myAddressViewInterface?.onActionButtonClick()
        }

    }

    fun setMyAddressViewListener(myAddressViewInterface: MyAddressViewInterface) {
        this.myAddressViewInterface = myAddressViewInterface
    }

    interface MyAddressViewInterface {
        fun onActionButtonClick()
    }
}