package com.sampurna.pocketmoney.mlm.ui.mobilerecharge.simpleui

import android.os.Bundle
import androidx.core.view.isVisible
import com.sampurna.pocketmoney.databinding.ActivityNewRechargeBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
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