package com.sampurna.pocketmoney.mlm.ui.playrecharge

import android.os.Bundle
import com.sampurna.pocketmoney.databinding.ActivityGooglePlayRechargeBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GooglePlayRecharge :
    BaseActivity<ActivityGooglePlayRechargeBinding>(ActivityGooglePlayRechargeBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarPlayRecharge.setApplicationToolbarListener(this)

    }


    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

}