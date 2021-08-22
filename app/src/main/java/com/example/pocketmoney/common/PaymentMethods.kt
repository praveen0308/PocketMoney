package com.example.pocketmoney.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPaymentMethodsBinding
import com.example.pocketmoney.shopping.adapters.MasterPaymentMethodAdapter
import com.example.pocketmoney.shopping.adapters.PaymentMethodAdapter
import com.example.pocketmoney.shopping.model.ModelMasterPaymentMethod
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.myEnums.PaymentEnum


class PaymentMethods :
    BaseBottomSheetDialogFragment<FragmentPaymentMethodsBinding>(FragmentPaymentMethodsBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface {

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvPaymentMethods()

    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter = PaymentMethodAdapter(getPaymentMethods(),this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }

    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.WALLET, "Wallet", R.drawable.ic_logo))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        paymentMethods.add(
            ModelPaymentMethod(
                PaymentEnum.PAYTM,
                "Other Payment",
                R.drawable.ic_paytm_logo,
                true
            )
        )



        return paymentMethods
    }


    override fun subscribeObservers() {

    }

    override fun onPaymentModeSelected(item: ModelPaymentMethod) {

    }


}