package com.sampurna.pocketmoney.shopping.ui.checkoutorder

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentApplyCouponCodeBinding
import com.sampurna.pocketmoney.shopping.model.DiscountModel
import com.sampurna.pocketmoney.shopping.repository.CheckoutRepository
import com.sampurna.pocketmoney.shopping.viewmodel.ApplyCouponCodeViewModel
import com.sampurna.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ApplyCouponCode : BaseFragment<FragmentApplyCouponCodeBinding>(FragmentApplyCouponCodeBinding::inflate) {

    private val viewModel by viewModels<ApplyCouponCodeViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository

    override fun onResume() {
        super.onResume()


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etCouponCode.doOnTextChanged { text, start, before, count ->
            checkoutRepository.appliedCouponCode.postValue("")
            binding.discountLayout.btnApply.text = "Apply"
            binding.discountLayout.btnApply.icon = null
            viewModel.couponCode.postValue(text.toString())
        }



        binding.btnSearch.setOnClickListener {
            viewModel.validateCouponCode(viewModel.couponCode.value!!)

        }

        binding.discountLayout.btnApply.setOnClickListener {
            checkoutRepository.appliedCouponCode.postValue(viewModel.couponCode.value!!)
            binding.discountLayout.btnApply.apply {
//                setTextColor(ContextCompat.getColor(context, R.color.Green))
                text = "Applied"
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_check_24)
            }
        }
    }

    override fun subscribeObservers() {

        viewModel.couponCode.observe(this, {
            binding.discountLayout.root.isVisible = false
            binding.btnSearch.isEnabled = !it.isNullOrEmpty()

        })
        viewModel.isValidCoupon.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

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


        viewModel.couponDetail.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateCouponDetails(it)
//
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

    private fun populateCouponDetails(discountModel: DiscountModel) {
        binding.discountLayout.root.isVisible = true
        binding.discountLayout.apply {
            checkoutRepository.appliedCouponCode.postValue(discountModel.Code)
            checkoutRepository.isFixed.postValue(discountModel.IsFixed)
            checkoutRepository.appliedDiscount.postValue(discountModel.Amount)
            if (discountModel.IsFixed) {
                tvDiscountAmount.text = "₹${discountModel.Amount}"
            } else {
                tvDiscountAmount.text = "${discountModel.Amount}%"
            }
            tvDiscountCode.text = discountModel.Code.toString()
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

}