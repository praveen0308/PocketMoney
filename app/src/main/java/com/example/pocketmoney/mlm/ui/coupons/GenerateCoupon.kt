package com.example.pocketmoney.mlm.ui.coupons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.FragmentGenerateCouponBinding
import com.example.pocketmoney.mlm.viewmodel.ManageCouponsViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.myEnums.PaymentEnum

class GenerateCoupon : BaseBottomSheetDialogFragment<FragmentGenerateCouponBinding>(FragmentGenerateCouponBinding::inflate),
    PaymentMethods.PaymentMethodsInterface {

    private val viewModel by viewModels<ManageCouponsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnIncrement.setOnClickListener {
                viewModel.incrementNoOfCoupons()
            }

            btnDecrement.setOnClickListener {
                viewModel.decrementNoOfCoupons()
            }

            btnPay.setOnClickListener {
                val sheet = PaymentMethods(this@GenerateCoupon)
                sheet.show(parentFragmentManager,sheet.tag)
            }
        }
    }
    override fun subscribeObservers() {
        viewModel.noOfCoupons.observe(viewLifecycleOwner,{
            binding.tvNoOfCoupons.text = it.toString()
            binding.btnPay.text = "Pay ₹${it*300}"
        })
    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {

    }


}