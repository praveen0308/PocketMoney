package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.sampurna.pocketmoney.databinding.FragmentAddBankBeneficiaryBinding
import com.sampurna.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.*
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

        binding.actvBank.setOnClickListener {
            val sheet = SelectBank()
            sheet.show(parentFragmentManager,sheet.tag)
        }
        binding.btnSubmit.setButtonClick {
            isAddedBeneficiary = true
            if (userId.isEmpty()){
                checkAuthorization()
            }else{
                binding.apply {
                    val accountNo = etAccountNumber.text.toString().trim()
                    val confirmAccountNo = etConfirmAccountNumber.text.toString().trim()
                    val ifsc = etIfscCode.text.toString().trim()
                    val customerName = etCustomerName.text.toString().trim()

                    if(accountNo.isEmpty() || accountNo.length<9){
                        validateEditText(binding.tilAccountNumber,"Enter valid account no.")
                    }else{
                        validateEditText(binding.tilAccountNumber)
                        if (confirmAccountNo != accountNo){
                            validateEditText(binding.tilConfirmAccountNo,"Account number is not matching!!!")
                        }else{
                            validateEditText(binding.tilConfirmAccountNo)
                            if (ifsc.length!=11){
                                validateEditText(binding.tilIfscCode,"IFSC code should be of 11 characters.")
                            }else{
                                validateEditText(binding.tilIfscCode)
                                if (customerName.isEmpty() || customerName.length<3){
                                    validateEditText(binding.tilCustomerName,"Enter valid customer name!!!")
                                }else{
                                    validateEditText(binding.tilCustomerName)
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
                        }
                    }

                }
            }


        }

        viewModel.getBanks()
    }



    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userId=it
        })

        viewModel.selectedBankIfsc.observe(viewLifecycleOwner,{
            if (it.isNotEmpty()){
                binding.etIfscCode.setText(it.toString())
            }
        })
        viewModel.selectedBank.observe(viewLifecycleOwner,{
            if (it.isNotEmpty()){
                binding.actvBank.setText(it.toString())
            }
        })
        /*viewModel.banks.observe(this, { _result ->
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
        })*/


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

/*    private fun populateBankDropdown(banks:List<BankModel>){
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
    }*/

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