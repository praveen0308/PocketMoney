package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAddPayoutCustomerBinding
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutCustomer
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.showActionDialog
import com.jmm.brsap.dialog_builder.DialogType

import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.util.*

@AndroidEntryPoint
class AddPayoutCustomer : BaseBottomSheetDialogFragment<FragmentAddPayoutCustomerBinding>(FragmentAddPayoutCustomerBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var userId = ""
    private var isAddedCustomer = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            isAddedCustomer = true
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val pinCode = binding.etPincode.text.toString().trim()

            if (firstName.isEmpty()){
                binding.tilFirstName.error = "First name cannot be empty."
            }else{
                binding.tilFirstName.error = null
                if (lastName.isEmpty()){
                    binding.tilLastName.error = "Last name cannot be empty."
                }else{
                    binding.tilLastName.error = null
                    if (address.isEmpty()){
                        binding.tilAddress.error = "Address cannot be empty."
                    }else{
                        binding.tilAddress.error = null
                        if (pinCode.isEmpty()){
                            binding.tilPincode.error = "Pincode cannot be empty."
                        }else{
                            binding.tilPincode.error = null
                            viewModel.addPayoutCustomer(PayoutCustomer(
                                FirstName = firstName,
                                LastName = lastName,
                                Address = address,
                                PinCode = pinCode,
                                TransferLimit = 200000.0,
                                IsActive = true,
                                AddedBy = userId,
                                AddedOn = Date().toString(),
                                CustomerTypeID = 1,
                                PayoutCustomerID = viewModel.customerNumber.value,
                                MobileNo = viewModel.customerNumber.value

                            ))
                        }
                    }
                }
            }
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userId=it
        })

        viewModel.addPayoutCustomer.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (isAddedCustomer){
                            dismiss()
                            showActionDialog(requireActivity(), DialogType.SUCCESS,
                                "Successfully Added !!",
                                "Customer ${viewModel.customerNumber.value} added successfully!!!",
                                "Great!"
                            ) {
                                viewModel.searchPayoutCustomer(viewModel.customerNumber.value!!)

                            }
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