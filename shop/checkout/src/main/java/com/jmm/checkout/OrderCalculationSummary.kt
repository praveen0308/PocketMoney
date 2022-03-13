package com.jmm.checkout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.jmm.checkout.databinding.FragmentOrderCalculationSummaryBinding
import com.jmm.core.utils.setAmount
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderCalculationSummary : BaseFragment<FragmentOrderCalculationSummaryBinding>(FragmentOrderCalculationSummaryBinding::inflate) {

    private val viewModel by activityViewModels<CheckoutViewModel>()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun subscribeObservers() {
        binding.apply {
            viewModel.noOfItems.observe(viewLifecycleOwner){
                tvNoOfItems.text = "($it)"
            }
            viewModel.originalAmount.observe(viewLifecycleOwner){
               tvProductOldPrice.setAmount(it.toString())
            }
            viewModel.savingAmount.observe(viewLifecycleOwner){
                tvTotalSaving.setAmount("-$it")
            }
            viewModel.totalAmount.observe(viewLifecycleOwner){
                tvTotalAmount.setAmount(it)
            }

            viewModel.shippingCharge.observe(viewLifecycleOwner){
                tvDeliveryCharges.setAmount("+$it")
            }
            viewModel.appliedDiscount.observe(viewLifecycleOwner){
                tvExtraDiscount.setAmount("-$it")
            }
            viewModel.tax.observe(viewLifecycleOwner){
                tvTax.setAmount("+$it")
            }

            viewModel.grandTotalAmount.observe(viewLifecycleOwner){
                tvAmountPayable.setAmount("$it")
            }
        }

    }


}