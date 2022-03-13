package com.jmm.checkout.apply_coupon

import android.os.Bundle
import android.view.View
import com.jmm.checkout.databinding.FragmentApplyDiscountCouponBinding
import com.jmm.model.shopping_models.DiscountModel
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.util.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ApplyDiscountCoupon : BaseBottomSheetDialogFragment<FragmentApplyDiscountCouponBinding>(FragmentApplyDiscountCouponBinding::inflate) {

//    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository

    private var isValidCoupon = false

    private var couponCode = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAction.setOnClickListener {

            if (isValidCoupon){
                
            }else{
                couponCode = binding.etCouponPin.text.toString().trim()
                if (couponCode.isNotEmpty()){
//                    viewModel.validateCouponCode(couponCode)
                }else{
                    showToast("Enter coupon code !!!!")
                }
            }

        }
    }
    override fun subscribeObservers() {

   /*     viewModel.isValidCoupon.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        isValidCoupon = it

                        if (isValidCoupon) {
                            checkoutRepository.appliedCouponCode.postValue(couponCode)
                            binding.imageView22.isVisible = false
                            binding.textView41.isVisible = false
                            viewModel.getCouponDetails(binding.etCouponPin.text.toString().trim())
                        } else {
                            binding.imageView22.isVisible = true
                            binding.textView41.isVisible = true
                            binding.btnAction.setState(LoadingButton.LoadingStates.RETRY, "Retry")
                        }
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    binding.btnAction.setState(
                        LoadingButton.LoadingStates.LOADING,
                        msg = "Validating"
                    )
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        }
        viewModel.couponDetail.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateCouponDetails(it)
                        if (isValidCoupon) {
                            if (it.IsFixed) {
                                checkoutRepository.appliedDiscount.postValue(it.Amount!!)
                            } else {
//                                checkoutRepository.appliedDiscount = calculatePercentageAmount(checkoutRepository.totalAmount,it.Amount!!)
                            }

                            binding.btnAction.setState(LoadingButton.LoadingStates.SUCCESS, "Apply")
                        }
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
        }

*/
    }

    private fun populateCouponDetails(discountModel: DiscountModel) {
  /*      binding.discountLayout.apply {
            if (discountModel.IsFixed){
                tvDiscountAmount.text = "₹${discountModel.Amount}"
            }else{
                tvDiscountAmount.text = "${discountModel.Amount}%"
            }
            tvDiscountCode.text = discountModel.Code.toString()
            tvDiscountName.text = discountModel.Name.toString()
            tvDiscountValidity.text = "${convertISOTimeToAny(discountModel.Starts_At.toString(),
                SDF_dM)} - ${convertISOTimeToAny(discountModel.Ends_At.toString(),
                SDF_dM)}"
        }*/
    }

    fun calculatePercentageAmount(amount:Double,percentage:Double):Double{
        return (amount*percentage)/100
    }

}