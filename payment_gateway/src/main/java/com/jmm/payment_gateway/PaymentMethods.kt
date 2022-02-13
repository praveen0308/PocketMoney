package com.jmm.payment_gateway

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.shopping_models.ModelPaymentMethod
import com.jmm.payment_gateway.databinding.FragmentPaymentMethodsBinding
import com.jmm.util.BaseBottomSheetDialogFragment


class PaymentMethods(private val paymentMethodsInterface: PaymentMethodsInterface, val isCod:Boolean = false,val isOnline:Boolean=true) :
    BaseBottomSheetDialogFragment<FragmentPaymentMethodsBinding>(FragmentPaymentMethodsBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface {

    private var selectedPaymentMethod = PaymentEnum.WALLET

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvPaymentMethods()

        binding.btnActivate.setOnClickListener {
            paymentMethodsInterface.onPaymentMethodSelected(selectedPaymentMethod)
            dismiss()
        }

    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter =
            PaymentMethodAdapter(getPaymentMethods(), this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }

    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.WALLET, "Wallet", R.drawable.ic_logo,true))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        if (isOnline) paymentMethods.add(
            ModelPaymentMethod(
                PaymentEnum.PAYTM,
                "Payment gateway",
                R.drawable.ic_paytm

            )
        )

        if (isCod){
            paymentMethods.add(
                ModelPaymentMethod(
                    PaymentEnum.COD,
                    "Cash On Delivery",
                    0

                )
            )

        }



        return paymentMethods
    }


    override fun subscribeObservers() {

    }

    override fun onPaymentModeSelected(item: ModelPaymentMethod) {
        selectedPaymentMethod = item.method

    }

    interface PaymentMethodsInterface{
        fun onPaymentMethodSelected(method:PaymentEnum)
    }

}