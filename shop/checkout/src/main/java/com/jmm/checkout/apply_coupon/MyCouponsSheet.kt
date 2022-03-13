package com.jmm.checkout.apply_coupon

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.checkout.databinding.FragmentMyCouponsSheetBinding
import com.jmm.model.shopping_models.DiscountCouponModel
import com.jmm.util.BaseBottomSheetDialogFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCouponsSheet(private val myCouponsListener: MyCouponsListener) :
    BaseBottomSheetDialogFragment<FragmentMyCouponsSheetBinding>(FragmentMyCouponsSheetBinding::inflate),
    MyCouponsAdapter.MyCouponsInterface {

    private var userId = ""
    private var roleId = 0
    private lateinit var myCouponsAdapter: MyCouponsAdapter
    private lateinit var selectedCoupon: DiscountCouponModel
    private val viewModel by viewModels<MyCouponsViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvCoupons()
        binding.btnApplyCoupon.setOnClickListener {
            myCouponsListener.onCouponSelected(
                selectedCoupon.CouponCode,
                selectedCoupon.IsFixed!!,
                selectedCoupon.Amount!!
            )
            dismiss()
        }
    }

    private fun setupRvCoupons() {
        myCouponsAdapter = MyCouponsAdapter(this)
        binding.rvCoupons.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = myCouponsAdapter
        }
    }
    private fun showLoading(v:Boolean){
        binding.progressBar.isVisible = v
    }
    override fun subscribeObservers() {
        viewModel.userID.observe(viewLifecycleOwner) {
            userId = it
        }
        viewModel.userRoleID.observe(viewLifecycleOwner) {
            roleId = it
            viewModel.getCoupons(userId, roleId)
        }

        viewModel.coupons.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isEmpty()) {
                            binding.apply {
                                btnApplyCoupon.isVisible = false
                                layoutNoCoupons.isVisible = true
                            }
                        } else {
                            binding.layoutNoCoupons.isVisible = false
                            myCouponsAdapter.setCouponModelList(it)
                        }
                    }
                    showLoading(false)
                }
                Status.LOADING -> {
                    showLoading(true)
                }
                Status.ERROR -> {
                    showLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        }

    }

    override fun onSelected(item: DiscountCouponModel) {
        selectedCoupon = item
    }

    interface MyCouponsListener {
        fun onCouponSelected(couponCode: String?, isFixed: Boolean, discountValue: Double?)
    }
}