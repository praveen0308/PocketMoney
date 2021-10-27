package com.example.pocketmoney.mlm.ui.forgotpassword

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentVerifyOtpBinding
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyOtp : BaseFragment<FragmentVerifyOtpBinding>(FragmentVerifyOtpBinding::inflate) {

    private val viewModel by activityViewModels<ForgotPasswordViewModel>()
    private lateinit var enteredOtp :String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpTextView.otpListener = object : OTPListener{
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                enteredOtp= otp
                binding.otpTextView.isEnabled = otp.length==6
            }
        }

        binding.btnVerify.setOnClickListener {
            viewModel.confirmOtp(viewModel.enterUserId,enteredOtp)
        }
    }
    override fun subscribeObservers() {
        viewModel.confirmOtpResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it){
                            showToast("Otp Verified!!!")
                            findNavController().navigate(VerifyOtpDirections.actionVerifyOtpToSetNewPassword())

                        }else{
                            showToast("Invalid Otp!!!")
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