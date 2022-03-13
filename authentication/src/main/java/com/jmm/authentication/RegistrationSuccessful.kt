package com.jmm.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jmm.authentication.databinding.FragmentRegistrationSuccessfulBinding
import com.jmm.util.BaseFullScreenDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationSuccessful : BaseFullScreenDialogFragment<FragmentRegistrationSuccessfulBinding>(FragmentRegistrationSuccessfulBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(requireActivity(),SignIn::class.java))
            dismiss()
            requireActivity().finish()
        }
    }
    override fun subscribeObservers() {

    }


}