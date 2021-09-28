package com.example.pocketmoney.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAuthInterceptorSheetBinding
import com.example.pocketmoney.mlm.ui.welcome.LoginFragment
import com.example.pocketmoney.mlm.ui.welcome.RegisterFragment
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
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