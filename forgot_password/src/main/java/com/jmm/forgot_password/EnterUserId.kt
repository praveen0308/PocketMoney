package com.jmm.forgot_password

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.forgot_password.databinding.FragmentEnterUserIdBinding
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterUserId : BaseFragment<FragmentEnterUserIdBinding>(FragmentEnterUserIdBinding::inflate) {

    private val viewModel by activityViewModels<ForgotPasswordViewModel>()
    private var userId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etUserId.doOnTextChanged { text, start, before, count ->
            binding.btnConfirm.isEnabled = text.toString().length == 10
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.enterUserId = binding.etUserId.text.toString().trim()

            viewModel.generatedOtp = (100000..999999).random().toString()
            viewModel.resetPassword(viewModel.enterUserId, viewModel.generatedOtp)
        }
    }

    override fun subscribeObservers() {
        viewModel.resetPasswordResponse.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            val msg =
                                "Your one time password for reset password is ${viewModel.generatedOtp} . Do not share this OTP to anyone for security reasons."
                            if (!viewModel.isNotified) {
                                viewModel.sendWhatsappMessage(viewModel.enterUserId, msg)
                            }
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
        }

        viewModel.isMessageSent.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> displayLoading(false)
                Status.LOADING -> displayLoading(true)
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        }

        viewModel.sendSmsResponse.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        findNavController().navigate(EnterUserIdDirections.actionEnterUserIdToVerifyOtp())
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
        }

    }

}