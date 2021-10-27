package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAddBankBeneficiaryBinding
import com.example.pocketmoney.mlm.model.payoutmodels.BankModel
import com.example.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.example.pocketmoney.mlm.viewmodel.AddBankBeneficiaryViewModel
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.shopping.model.ModelState
import com.example.pocketmoney.utils.*
import com.jmm.brsap.dialog_builder.DialogType

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBankBeneficiary : BaseFullScreenDialogFragment<FragmentAddBankBeneficiaryBinding>(FragmentAddBankBeneficiaryBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by activityViewModels<PayoutViewModel>()
    private lateinit var selectedBank : String
    private var userId : String = ""
    private var isAddedBeneficiary = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.btnSubmit.setState(LoadingButton.LoadingStates.DISABLED,mText = "Submit")
        binding.toolbarAddBeneficiary.setApplicationToolbarListener(this)
        binding.btnSubmit.setButtonClick {
            isAddedBeneficiary = true
            binding.apply {
                val accountNo = etAccountNumber.text.toString().trim()
                val confirmAccountNo = etConfirmAccountNumber.text.toString().trim()
                val ifsc = etIfscCode.text.toString().trim()
                val customerName = etCustomerName.text.toString().trim()

                    viewModel.addBeneficiary(Beneficiary(
                        Account = accountNo,
                        IFSCCode = ifsc,
                        BeneficiaryName = customerName,
                        AssociatedUser = userId,
                        CustomerID = viewModel.customerNumber.value,
                        Type= 1
                    ))
            }

        }

        viewModel.getBanks()
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userId=it
        })

        viewModel.banks.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateBankDropdown(it)
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


        viewModel.isBeneficiaryAdded.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
//                        showToast("Beneficiary added successfully !!!")
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

    private fun populateBankDropdown(banks:List<BankModel>){
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, banks)
        //actv is the AutoCompleteTextView from your layout file
        binding.actvBank.threshold = 1 //start searching for values after typing first character
        binding.actvBank.setAdapter(arrayAdapter)

        binding.actvBank.setOnItemClickListener { parent, view, position, id ->
            val bank = parent.getItemAtPosition(position) as BankModel
            bank.IFSC?.let {
                binding.etIfscCode.setText(it.toString())
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isAddedBeneficiary = false
    }

    override fun onToolbarNavClick() {
        dismiss()
    }

    override fun onMenuClick() {

    }
}