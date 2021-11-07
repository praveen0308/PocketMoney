package com.sampurna.pocketmoney.mlm.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sampurna.pocketmoney.databinding.FragmentEnterUserIdBinding
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterUserId : BaseFragment<FragmentEnterUserIdBinding>(FragmentEnterUserIdBinding::inflate) {

    private val viewModel by activityViewModels<ForgotPasswordViewModel>()
    private lateinit var generatedOtp : String
    private var userId = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etUserId.doOnTextChanged { text, start, before, count ->
            binding.btnConfirm.isEnabled = text.toString().length==10
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.enterUserId = binding.etUserId.text.toString().trim()

            generatedOtp = (0..999999).random().toString()
            viewModel.resetPassword(viewModel.enterUserId,generatedOtp)
        }
    }

    override fun subscribeObservers() {
        viewModel.resetPasswordResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it){
                            val msg = "Your one time password for reset password is ${generatedOtp.toString()} . Do not share this OTP to anyone for security reasons."
                            viewModel.sendWhatsappMessage(viewModel.enterUserId,msg)
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

        viewModel.isMessageSent.observe(this, { _result ->
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
        })
    }

}