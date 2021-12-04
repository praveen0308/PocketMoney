package com.sampurna.pocketmoney.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sampurna.pocketmoney.authentication.SignIn
import com.sampurna.pocketmoney.authentication.SignUp
import com.sampurna.pocketmoney.databinding.FragmentAuthInterceptorSheetBinding

import com.sampurna.pocketmoney.utils.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthInterceptorSheet : BaseBottomSheetDialogFragment<FragmentAuthInterceptorSheetBinding>(FragmentAuthInterceptorSheetBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSignIn.setOnClickListener {
                startActivity(Intent(requireActivity(), SignIn::class.java))
            }

            btnSignUp.setOnClickListener {
                startActivity(Intent(requireActivity(), SignUp::class.java))

            }
        }
    }
    override fun subscribeObservers() {

    }

}