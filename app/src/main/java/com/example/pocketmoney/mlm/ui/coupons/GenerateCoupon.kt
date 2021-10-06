package com.example.pocketmoney.mlm.ui.coupons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.FragmentGenerateCouponBinding
import com.example.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.example.pocketmoney.mlm.viewmodel.ManageCouponsViewModel
import com.example.pocketmoney.paymentgateway.PaymentPortal
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateCoupon : BaseBottomSheetDialogFragment<FragmentGenerateCouponBinding>(FragmentGenerateCouponBinding::inflate),
    PaymentPortal.PaymentPortalCallback {

    private val viewModel by viewModels<ManageCouponsViewModel>()
    private var amountPayable = 0.0

    private var selectedMethod = PaymentEnum.WALLET
    private var userId = ""

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
                val sheet = PaymentPortal(this@GenerateCoupon,amountPayable)
                sheet.show(parentFragmentManager,sheet.tag)
            }
        }
    }
    override fun subscribeObservers() {
        viewModel.noOfCoupons.observe(viewLifecycleOwner,{
            amountPayable = (it*300).toDouble()
            binding.tvNoOfCoupons.text = it.toString()
            binding.btnPay.setText("Pay ₹$amountPayable")
        })

        viewModel.userId.observe(viewLifecycleOwner,{
            if (it.isNullOrEmpty()){

            }
            else{
                userId = it
            }
        })

        viewModel.generateCouponResponse.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Coupons generated successfully !!!!")
                        requireActivity().finish()
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
    }

    override fun onPaymentResultReceived(
        method: PaymentEnum,
        result: Boolean,
        message: String,
        paytmResponseModel: PaytmResponseModel?
    ) {
        selectedMethod = method
        if (result){
            when(selectedMethod){
                PaymentEnum.WALLET->viewModel.generateNewCoupons(userId,1,viewModel.noOfCoupons.value!!)
                PaymentEnum.PCASH->viewModel.generateNewCoupons(userId,2,viewModel.noOfCoupons.value!!)
                PaymentEnum.PAYTM->viewModel.generateNewCoupons(userId,3,viewModel.noOfCoupons.value!!)
            }
        }
        else{
            showToast("Cancelled !!")
        }
    }


}