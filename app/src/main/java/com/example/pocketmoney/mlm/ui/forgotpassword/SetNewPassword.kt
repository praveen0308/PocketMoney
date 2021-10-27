package com.example.pocketmoney.mlm.ui.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSetNewPasswordBinding
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetNewPassword : BaseFragment<FragmentSetNewPasswordBinding>(FragmentSetNewPasswordBinding::inflate) {
    private val viewModel by activityViewModels<ForgotPasswordViewModel>()
    private lateinit var newPassword : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etPassword.doOnTextChanged { text, start, before, count ->
            newPassword = text.toString().trim()
            if (text.toString().length > 6){
                binding.tilPassword.error = null
                binding.btnConfirm.isEnabled = true
            }else{
                binding.tilPassword.error = "Password must have at least 6 characters !!"
                binding.btnConfirm.isEnabled = false
            }
        }
        binding.etConfirmPassword.doOnTextChanged { text, start, before, count ->
            if (text.toString() == binding.etPassword.text.toString().trim()){
                binding.tilConfirmPassword.error = null
                binding.btnConfirm.isEnabled = true
            }else{
                binding.tilConfirmPassword.error = "Password doesn't match !!!"
                binding.btnConfirm.isEnabled = false
            }

        }
        binding.btnConfirm.setOnClickListener {
            viewModel.changePassword(viewModel.enterUserId,newPassword)
        }
    }

    override fun subscribeObservers() {
        viewModel.changePasswordResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it){
                            showToast("Password changed successfully !!!")
                            requireActivity().finish()
                        }else{
                            showToast("Password resetting failed !!!")

                        }
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

}
