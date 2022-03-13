package com.jmm.checkout.apply_coupon

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.jmm.checkout.CheckoutViewModel
import com.jmm.checkout.databinding.FragmentApplyCouponCodeBinding
import com.jmm.core.utils.SDF_d_M_y
import com.jmm.core.utils.convertISOTimeToAny
import com.jmm.model.shopping_models.DiscountModel
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyCouponCode :
    BaseFragment<FragmentApplyCouponCodeBinding>(FragmentApplyCouponCodeBinding::inflate),
    MyCouponsSheet.MyCouponsListener {

    private val viewModel by viewModels<ApplyCouponCodeViewModel>()
    private val checkoutViewModel by activityViewModels<CheckoutViewModel>()


    //    private lateinit var discountCoupon: DiscountModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etCouponCode.doOnTextChanged { text, start, before, count ->
//            checkoutRepository.appliedCouponCode.postValue("")
            /*binding.discountLayout.btnApply.text = "Apply"
            binding.discountLayout.btnApply.icon = null*/
            viewModel.couponCode.postValue(text.toString())
        }


        binding.btnMyCoupons.setOnClickListener {
            val sheet = MyCouponsSheet(this)
            sheet.show(parentFragmentManager, sheet.tag)
        }
        binding.btnSearch.setOnClickListener {
            viewModel.validateCouponCode(viewModel.couponCode.value!!)
        }

        binding.discountLayout.btnApply.apply {
            text = if (checkoutViewModel.isCouponApplied) "Remove" else "Apply"
        }
        binding.discountLayout.btnApply.setOnClickListener {
            checkoutViewModel.isCouponApplied = !checkoutViewModel.isCouponApplied

            if (checkoutViewModel.isCouponApplied) {
                checkoutViewModel.setDiscount()

            } else {
                checkoutViewModel.removeDiscount()
            }

            binding.discountLayout.btnApply.apply {
                text = if (checkoutViewModel.isCouponApplied) "Remove" else "Apply"
            }

        }


    }

    override fun subscribeObservers() {

        viewModel.couponCode.observe(this) {
            binding.btnSearch.isEnabled = !it.isNullOrEmpty()

        }
        checkoutViewModel.appliedCoupon.observe(viewLifecycleOwner) {
            if (it.Code.isNotEmpty()) {
                populateCouponDetails(it)
//                checkoutViewModel.appliedDiscount.postValue(it.Amount)
                binding.etCouponCode.setText(it.Code)
            } else {
                binding.etCouponCode.setText("")
                binding.discountLayout.root.isVisible = false
            }
        }
        viewModel.pageState.observe(viewLifecycleOwner) { state ->
            displayLoading(false)
            when (state) {
                ApplyCouponCodePageState.CouponNotValid -> showToast("Invalid Coupon !!!!")
                ApplyCouponCodePageState.CouponValid -> {}
                is ApplyCouponCodePageState.Error -> showToast(state.msg)
                ApplyCouponCodePageState.Idle -> {}
                ApplyCouponCodePageState.Loading -> displayLoading(true)
                is ApplyCouponCodePageState.ReceivedCouponDetails -> {
                    checkoutViewModel.appliedCoupon.postValue(state.discountCoupon)

                }
            }

        }

    }

    private fun populateCouponDetails(discountModel: DiscountModel) {
        binding.discountLayout.root.isVisible = true
        binding.discountLayout.apply {

            if (discountModel.IsFixed) {
                tvDiscountAmount.text = "₹${discountModel.Amount}"
            } else {
                tvDiscountAmount.text = "${discountModel.Amount}%"
            }
            tvDiscountCode.text = discountModel.Code
            tvDiscountName.text = discountModel.Name.toString()
            tvDiscountValidity.text = "${
                convertISOTimeToAny(
                    discountModel.Starts_At.toString(),
                    SDF_d_M_y
                )
            } - ${
                convertISOTimeToAny(
                    discountModel.Ends_At.toString(),
                    SDF_d_M_y
                )
            }"
        }
    }

    override fun onCouponSelected(couponCode: String?, isFixed: Boolean, discountValue: Double?) {
        if (couponCode != null && discountValue != null) {
//            viewModel.validateCouponCode(couponCode)
            viewModel.couponCode.postValue(couponCode)
            binding.etCouponCode.setText(couponCode.toString())
//            checkoutViewModel.setDiscount(couponCode,isFixed,discountValue)
        }
    }

}