package com.jmm.kyc.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentEnterAadhaarNumberBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint





@AndroidEntryPoint
class EnterAadhaarNumber : BaseFragment<FragmentEnterAadhaarNumberBinding>(FragmentEnterAadhaarNumberBinding::inflate) {

    private val viewModel by activityViewModels<KycViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etAadhaarNumber.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                binding.btnNext.isEnabled = text.length==12
            }
        }

        binding.btnNext.setOnClickListener {
            viewModel.documentNumber = binding.etAadhaarNumber.text.toString().trim()
            findNavController().navigate(EnterAadhaarNumberDirections.actionEnterAadhaarNumberToUploadKycPages())
        }

    }

    override fun subscribeObservers() {

    }

}