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
import com.sampurna.pocketmoney.utils.Status
import com.sampurna.pocketmoney.mlm.viewmodel.LoginViewModel
import com.sampurna.pocketmoney.utils.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class LoginFragment : BaseBottomSheetDialogFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {


    private val viewModel : LoginViewModel by viewModels()

    private lateinit var dialog: android.app.AlertDialog

//    private lateinit var navController:NavController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        navController = Navigation.findNavController(view)

        createProgressDialog()
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(requireActivity(),ForgotPassword::class.java))
        }

        binding.btnRegister.setOnClickListener {
//            navController.navigate(R.id.action_loginFragment_to_registerFragment)
            val sheet = RegisterFragment()
            sheet.show(parentFragmentManager,sheet.tag)
            dismiss()
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


    override fun subscribeObservers(){
        viewModel.userModel.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        try {
                            viewModel.updateWelcomeStatus(LOGIN_DONE)
                            viewModel.updateLoginId(it.LoginID!!)
                            viewModel.updateUserId(it.UserID!!)
                            viewModel.updateUserName(it.UserName!!)
                            viewModel.updateUserRoleID(it.UserRoleID!!)
                        }finally {
                            val intent = Intent(requireActivity(), MainDashboard::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            requireActivity().finish()
                        }


                    }
                    dialog.hide()
                }
                Status.LOADING -> {
                    dialog.setMessage("Logging in...")
                    dialog.show()
                }
                Status.ERROR -> {
                    dialog.hide()
                    _result.message?.let {
                        displayError(it)
                    }
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