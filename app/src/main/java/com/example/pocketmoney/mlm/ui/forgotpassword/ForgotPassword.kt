package com.example.pocketmoney.mlm.ui.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityForgotPasswordBinding
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
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