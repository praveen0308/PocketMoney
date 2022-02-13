package com.jmm.coupon

import android.os.Bundle
import com.jmm.coupon.databinding.ActivityManageCouponBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCoupon : BaseActivity<ActivityManageCouponBinding>(ActivityManageCouponBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarManageCoupons.setApplicationToolbarListener(this)
    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}