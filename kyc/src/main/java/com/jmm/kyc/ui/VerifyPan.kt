package com.jmm.kyc.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jmm.kyc.KycPageState
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentVerifyPanBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyPan : BaseFragment<FragmentVerifyPanBinding>(FragmentVerifyPanBinding::inflate) {

    private val viewModel by viewModels<KycViewModel>()

    private var userId = ""

    private var isVerified = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnVerifyPan.setOnClickListener {
            val panNumber = binding.etPanNumber.text.toString().trim()
            if (isVerified) viewModel.updateCustomerPanKycDetail(
                userId,
                binding.etPanNumber.text.toString(),
                binding.etPanName.text.toString()
            )
            else viewModel.verifyPanNumber(panNumber)

        }
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner) {
            userId = it
        }
        viewModel.verifyPanPageState.observe(viewLifecycleOwner) { state ->
            displayLoading(false)
            hideLoadingDialog()
            when (state) {
                is KycPageState.Error -> displayError(state.msg)
                KycPageState.Idle -> {}
                KycPageState.InvalidPan -> {
                    displayError("Invalid PAN")
                }
                KycPageState.Loading -> {
                    displayLoading(true)
                }
                is KycPageState.PanVerified -> {
                    binding.apply {
                        etPanNumber.isEnabled = false
                        etPanName.setText(state.response.panData!!.full_name)
                        isVerified = true
                        btnVerifyPan.text = "Submit"
                    }
                }
                is KycPageState.Processing -> {
                    showLoadingDialog(
                        state.msg
                    )
                }
                KycPageState.PanUpdatedSuccessfully -> {
                    showToast("Pan verified successfully!!!")
                    findNavController().navigateUp()
                }
            }
        }
    }

}