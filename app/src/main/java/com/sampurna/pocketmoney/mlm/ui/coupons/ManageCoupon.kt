package com.sampurna.pocketmoney.mlm.ui.coupons

import android.os.Bundle
import com.sampurna.pocketmoney.databinding.ActivityManageCouponBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
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