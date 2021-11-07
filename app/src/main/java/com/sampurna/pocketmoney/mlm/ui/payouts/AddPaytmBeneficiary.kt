package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import com.sampurna.pocketmoney.databinding.FragmentAddPaytmBeneficiaryBinding
import com.sampurna.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.*
import com.jmm.brsap.dialog_builder.DialogType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPaytmBeneficiary : BaseBottomSheetDialogFragment<FragmentAddPaytmBeneficiaryBinding>(FragmentAddPaytmBeneficiaryBinding::inflate) {
    private val viewModel by activityViewModels<PayoutViewModel>()
    private var userId : String = ""
    private var isAddedBeneficiary = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.payoutType.value == 2){
            binding.etUpiId.setHint("Enter UPI Id")
            binding.etUpiId.inputType = EditorInfo.TYPE_CLASS_TEXT


        }else{
            binding.etUpiId.setHint("Paytm Number")
            binding.etUpiId.inputType = EditorInfo.TYPE_CLASS_NUMBER
        }
        binding.btnSubmit.setButtonClick {
            if (userId.isEmpty()){
                checkAuthorization()
            }else{
                binding.apply {
                    val upiID = etUpiId.text.toString().trim()
                    val customerName = etCustomerName.text.toString().trim()
                    isAddedBeneficiary = true

                    val upiIdPattern = Regex("[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}")
                    if(upiID.matches(upiIdPattern)){
                        validateEditText(tilUpiId)
                        if (customerName.isEmpty() || customerName.length<3){
                            validateEditText(binding.tilCustomerName,"Enter valid customer name!!!")
                        }else{
                            validateEditText(binding.tilCustomerName)
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

                    }else{
                        validateEditText(tilUpiId,"Enter valid upi id!! for example:abcd@kotak,123@paytm")
                    }

                }
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


                        if (isAddedBeneficiary){
                            dismiss()
                            binding.btnSubmit.setState(LoadingButton.LoadingStates.SUCCESS,msg = "Added Successfully !!")
                            showActionDialog(requireActivity(), DialogType.SUCCESS,
                                "Successfully Added !!",
                                "Beneficiary ${binding.etCustomerName.text.toString().trim()} added successfully!!!",
                                "Great!"
                            ) {

                                viewModel.getBeneficiaries(viewModel.customerNumber.value!!,viewModel.payoutType.value!!)

                            }

                        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        isAddedBeneficiary = false
    }
}