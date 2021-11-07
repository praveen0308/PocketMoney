package com.sampurna.pocketmoney.mlm.ui.forgotpassword

import android.os.Bundle
import com.sampurna.pocketmoney.databinding.ActivityForgotPasswordBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPassword : BaseActivity<ActivityForgotPasswordBinding>(ActivityForgotPasswordBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarForgotPassword.setApplicationToolbarListener(this)
    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}