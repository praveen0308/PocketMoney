package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAddPaytmBeneficiaryBinding
import com.example.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.LoadingButton
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPaytmBeneficiary : BaseBottomSheetDialogFragment<FragmentAddPaytmBeneficiaryBinding>(FragmentAddPaytmBeneficiaryBinding::inflate) {
    private val viewModel by activityViewModels<PayoutViewModel>()
    private var userId : String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setButtonClick {
            binding.apply {
                val upiID = etUpiId.text.toString().trim()

                val customerName = etCustomerName.text.toString().trim()

                viewModel.addBeneficiary(
                    Beneficiary(
                    Account = upiID,
                    BeneficiaryName = customerName,
                    AssociatedUser = userId,
                    CustomerID = viewModel.customerNumber.value,
                    Type= viewModel.payoutType.value
                )
                )
            }

        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userId=it
        })

        viewModel.isBeneficiaryAdded.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Beneficiary added successfully !!!")
                        binding.btnSubmit.setState(LoadingButton.LoadingStates.SUCCESS,msg = "Added Successfully !!")
                        dismiss()
                    }
                    displayLoading(false)

                }
                Status.LOADING -> {
                    displayLoading(true)
                    binding.btnSubmit.setState(LoadingButton.LoadingStates.LOADING,msg = "Adding new beneficiary...")
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