package com.jmm.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.jmm.authentication.databinding.ActivitySignInBinding
import com.jmm.navigation.NavRoute.ForgotPassword
import com.jmm.navigation.NavRoute.MainDashboard

import com.jmm.repository.UserPreferencesRepository
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity

import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class SignIn : BaseActivity<ActivitySignInBinding>(ActivitySignInBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var dialog: android.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createProgressDialog()
        binding.toolbarSignIn.setApplicationToolbarListener(this)
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, Class.forName(ForgotPassword)))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            if (!binding.etUsername.text.isNullOrBlank()) {
                if (!binding.etPassword.text.isNullOrBlank()) {
                    viewModel.doLogin(
                        binding.etUsername.text.toString().trim(),
                        binding.etPassword.text.toString().trim()
                    )
                } else showToast("Password cannot be empty !!")
            } else showToast("Enter valid username...")

        }
    }

    override fun subscribeObservers() {
        viewModel.pageState.observe(this) { state ->
            dialog.hide()
            when (state) {

                is LoginPageState.Idle -> {
                }
                is LoginPageState.Loading -> {
                    dialog.setMessage("Logging in...")
                    dialog.show()
                }
                is LoginPageState.LoginSuccessful -> {
                    try {
                        viewModel.updateWelcomeStatus(UserPreferencesRepository.LOGIN_DONE)
                        viewModel.updateLoginId(state.userModel.LoginID!!)
                        viewModel.updateUserId(state.userModel.UserID!!)
                        viewModel.updateUserName(state.userModel.UserName!!)
                        viewModel.updateUserRoleID(state.userModel.UserRoleID!!)
                        viewModel.updateUserStatus(state.userModel.BlockedStatus!!)

                    } finally {
                        if (state.userModel.BlockedStatus!!) {
                            showToast("You're blocked...")
                        } else {
                            viewModel.checkIsAccountActive(state.userModel.UserID!!)
                        }

                    }
                }
                is LoginPageState.GotAccountStatus -> {
                    viewModel.updateUserType(state.status)

                    val intent = Intent(this, Class.forName(MainDashboard))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is LoginPageState.Error -> {
                    showToast(state.msg)
                }
            }
        }

    }


    private fun createProgressDialog() {
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Logging In...")
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialog)
            .build()
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }


}