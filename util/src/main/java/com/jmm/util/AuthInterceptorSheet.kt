package com.jmm.util

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jmm.navigation.NavRoute.SignIn
import com.jmm.navigation.NavRoute.SignUp
import com.jmm.util.databinding.FragmentAuthInterceptorSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthInterceptorSheet : BaseBottomSheetDialogFragment<FragmentAuthInterceptorSheetBinding>(FragmentAuthInterceptorSheetBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSignIn.setOnClickListener {
                startActivity(Intent(requireActivity(),Class.forName(SignIn)))
            }

            btnSignUp.setOnClickListener {
                startActivity(Intent(requireActivity(), Class.forName(SignUp)))

            }
        }
    }
    override fun subscribeObservers() {

    }

}