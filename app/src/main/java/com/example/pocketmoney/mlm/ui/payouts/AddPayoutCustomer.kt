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
import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.util.*

@AndroidEntryPoint
class AddPayoutCustomer : BaseBottomSheetDialogFragment<FragmentAddPayoutCustomerBinding>(FragmentAddPayoutCustomerBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var userId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            viewModel.addPayoutCustomer(PayoutCustomer(
                FirstName = binding.etFirstName.text.toString().trim(),
                LastName = binding.etLastName.text.toString().trim(),
                Address = binding.etAddress.text.toString().trim(),
                PinCode = binding.etPincode.text.toString().trim(),
                TransferLimit = 200000.0,
                IsActive = true,
                AddedBy = userId.toInt(),
                AddedOn = Date().toString(),
                CustomerTypeID = 1,
                PayoutCustomerID = viewModel.customerNumber.value,
                MobileNo = userId

            ))
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
                        showToast("Customer added Successfully !!!")
                        dismiss()
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