package com.sampurna.pocketmoney.mlm.ui.forgotpassword

import android.os.Bundle
import androidx.activity.viewModels
import com.sampurna.pocketmoney.databinding.ActivityChangePasswordBinding
import com.sampurna.pocketmoney.mlm.viewmodel.ChangePasswordViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePassword :
    BaseActivity<ActivityChangePasswordBinding>(ActivityChangePasswordBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<ChangePasswordViewModel>()
    private var userId = ""
    private var newPassword = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            toolbarChangePassword.setApplicationToolbarListener(this@ChangePassword)
            btnConfirm.setOnClickListener {
                val oldPassword = etOldPassword.text.toString().trim()
                newPassword = etNewPassword.text.toString().trim()
                val newConfirmPassword = etConfirmNewPassword.text.toString().trim()

                if (newPassword.length < 6) {
                    tilNewPassword.error = "Password lenght should be greater than 6!!!"
                } else if (newConfirmPassword != newPassword) {
                    tilConfirmNewPassword.error = "Password is not matching!!!"
                } else {
                    tilNewPassword.error = null
                    tilConfirmNewPassword.error = null
                    viewModel.doLogin(userId, oldPassword)
                }

            }
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userId = it
        })

        viewModel.userModel.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.changePassword(userId, newPassword)
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

        viewModel.changePasswordResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Password changed successfully!!!")
                        finish()
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

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}