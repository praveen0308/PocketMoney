package com.jmm.forgot_password

import android.os.Bundle
import com.jmm.forgot_password.databinding.ActivityForgotPasswordBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
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

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            finish()
            super.onBackPressed()
            //additional code
        } else {
//            supportFragmentManager.popBackStack()
            finish()
        }
    }
}