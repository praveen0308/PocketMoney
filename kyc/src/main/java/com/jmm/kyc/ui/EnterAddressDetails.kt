package com.jmm.kyc.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentEnterAddressDetailsBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterAddressDetails : BaseFragment<FragmentEnterAddressDetailsBinding>(FragmentEnterAddressDetailsBinding::inflate) {

    private val viewModel by activityViewModels<KycViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSubmit.setOnClickListener {
                viewModel.address1 = etAddress1.text.toString().trim()
                viewModel.address2 = etAddress2.text.toString().trim()
                viewModel.pincode = etPincode.text.toString().trim()
                viewModel.state = etState.text.toString().trim()
                viewModel.city = etCity.text.toString().trim()

                findNavController().navigate(EnterAddressDetailsDirections.actionEnterAddressDetailsToSelectDocument())
            }
        }

    }

    override fun subscribeObservers() {

    }

}