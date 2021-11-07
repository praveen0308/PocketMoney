package com.sampurna.pocketmoney.common

import android.os.Bundle
import android.view.View
import com.sampurna.pocketmoney.databinding.FragmentAuthInterceptorSheetBinding

import com.sampurna.pocketmoney.mlm.ui.welcome.LoginFragment
import com.sampurna.pocketmoney.mlm.ui.welcome.RegisterFragment
import com.sampurna.pocketmoney.utils.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthInterceptorSheet : BaseBottomSheetDialogFragment<FragmentAuthInterceptorSheetBinding>(FragmentAuthInterceptorSheetBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSignIn.setOnClickListener {
                val sheet  = LoginFragment()
                sheet.show(parentFragmentManager,sheet.tag)
            }

            btnSignUp.setOnClickListener {
                val sheet  = RegisterFragment()
                sheet.show(parentFragmentManager,sheet.tag)
            }
        }
    }
    override fun subscribeObservers() {

    }

}