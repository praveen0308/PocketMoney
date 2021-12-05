package com.sampurna.pocketmoney.mlm.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentLoginBinding
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository.Companion.LOGIN_DONE
import com.sampurna.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.sampurna.pocketmoney.mlm.ui.forgotpassword.ForgotPassword
import com.sampurna.pocketmoney.mlm.viewmodel.LoginPageState
import com.sampurna.pocketmoney.mlm.viewmodel.LoginViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {


    private val viewModel: LoginViewModel by viewModels()

    private lateinit var dialog: android.app.AlertDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createProgressDialog()
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(requireActivity(),ForgotPassword::class.java))
        }

        binding.btnRegister.setOnClickListener {
//            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            /* val sheet = RegisterFragment()
             sheet.show(parentFragmentManager,sheet.tag)
             dismiss()*/
        }

        binding.btnSignIn.setOnClickListener{
            if (!binding.etUsername.text.isNullOrBlank()) {
                if (!binding.etPassword.text.isNullOrBlank()){
                viewModel.doLogin(
                    binding.etUsername.text.toString().trim(),
                    binding.etPassword.text.toString().trim()
                )}
                else Toast.makeText(context, "Password cannot be empty !!", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "Enter valid username..", Toast.LENGTH_SHORT).show()

        }
    }


    override fun subscribeObservers() {
        viewModel.pageState.observe(viewLifecycleOwner, { state ->
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
                        viewModel.updateWelcomeStatus(LOGIN_DONE)
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

                    val intent = Intent(requireActivity(), MainDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is LoginPageState.Error -> {
                    showToast(state.msg)
                }
            }
        })

    }


    private fun createProgressDialog(){
        dialog = SpotsDialog.Builder()
                .setContext(context)
                .setMessage("Logging In...")
                .setCancelable(false)
                .setTheme(R.style.CustomProgressDialog)
                .build()
    }



}