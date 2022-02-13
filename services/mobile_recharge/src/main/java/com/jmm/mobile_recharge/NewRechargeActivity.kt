package com.jmm.mobile_recharge

import android.os.Bundle
import androidx.core.view.isVisible
import com.jmm.mobile_recharge.databinding.ActivityNewRechargeBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewRechargeActivity : BaseActivity<ActivityNewRechargeBinding>(ActivityNewRechargeBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivityRecharge.setApplicationToolbarListener(this)

    }

    fun setToolbarVisibility(status:Boolean){
        binding.toolbarActivityRecharge.isVisible = status
    }
    override fun subscribeObservers() {

    }


    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}