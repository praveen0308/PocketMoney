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
import com.example.pocketmoney.databinding.FragmentEnterUserIdBinding
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.LoadingButton
import com.example.pocketmoney.utils.Status
import com.paytm.pgsdk.PaytmOrder
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
                            val msg = "Your pocketmoney One Time Password(OTP) is $generatedOtp. Do not share this OTP to anyone for security reasons."
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