package com.example.pocketmoney.mlm.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentRegisterBinding
import com.example.pocketmoney.mlm.model.ModelCustomerDetail
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.CustomValidator
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog


@AndroidEntryPoint
class RegisterFragment : BaseBottomSheetDialogFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {


    private lateinit var dialog: android.app.AlertDialog

    private lateinit var validator:CustomValidator

    // ViewModel
    private val accountViewModel : AccountViewModel by viewModels()

    // Variables
    private var userID : String = ""
    private var roleID : Int=0
    private lateinit var customerDetail: ModelCustomerDetail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initialUiState()
        initiateFieldsValidation()
        binding.btnRegister.setOnClickListener {
            binding.apply {
                if (etSponsorId.text.toString().length==10 && tilSponsorId.error==null){
                    if (validator.validateName(tilFullName,etFullName) &&
                            validator.validateEmail(tilEmail,etEmail) &&
                            validator.validateMobileNo(tilMobileNumber,etMobileNumber)
                            && validator.validatePincode(tilPincode,etPincode)){
                                if(cbTncAgreement.isChecked){
                                    customerDetail = ModelCustomerDetail(
                                            SponsorID = etSponsorId.text.toString().toDouble(),
                                            FullName = etFullName.text.toString(),
                                            EmailID = etEmail.text.toString(),
                                            Mobile = etMobileNumber.text.toString(),
                                            PinNo = etPincode.text.toString())
                                    accountViewModel.checkAccountAlreadyExist(etMobileNumber.text.toString())
                                    
                                }
                        else{
                                cbTncAgreement.requestFocus()
                        }


                    }
                }
                else{
                    etSponsorId.requestFocus()
                }
            }
        }
        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }



        binding.etSponsorId.doAfterTextChanged {
            if(it!!.length==10) accountViewModel.getSponsorName(it.toString())
        }
    }

    private fun initialUiState(){
        createProgressDialog("Verifying....")
        binding.apply {
            etSponsorId.setText(getString(R.string.default_sponsor_id))

        }

    }

    private fun initiateFieldsValidation(){
        validator = CustomValidator(requireActivity())
        binding.apply {
            etFullName.doAfterTextChanged { validator.validateName(tilFullName,etFullName) }
            etEmail.doAfterTextChanged { validator.validateEmail(tilEmail,etEmail) }
            etMobileNumber.doAfterTextChanged { validator.validateMobileNo(tilMobileNumber,etMobileNumber) }
            etPincode.doAfterTextChanged { validator.validatePincode(tilPincode,etPincode) }

        }

    }
    override fun subscribeObservers() {

        accountViewModel.sponsorName.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isEmpty())
                            binding.tilSponsorId.error = "Sponsor id does not exit !!!"
                        else
                            binding.tilSponsorId.error = null
                        binding.etSponsorName.setText(it)
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

        accountViewModel.isAccountDuplicate.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            if (customerDetail!=null) accountViewModel.registerUser(customerDetail)

                        } else {
                            Toast.makeText(context, "User Already Exist", Toast.LENGTH_SHORT).show()
                            displaySubmitting(false)
                        }
                    }
                }
                Status.LOADING -> {
                    dialog.setMessage("Verifying...")
                    displaySubmitting(true)
                }
                Status.ERROR -> {
                    displaySubmitting(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })


        accountViewModel.isSuccessfullyRegistered.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            displayError("Registration failed !!!")
                            displaySubmitting(false)
                        } else {
                            Toast.makeText(context, "Registered Successfully !!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                    }
                    displaySubmitting(false)
                }
                Status.LOADING -> {
                    dialog.setMessage("Registering...")
                    displaySubmitting(true)
                }
                Status.ERROR -> {
                    displaySubmitting(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })



    }

    private fun createProgressDialog(msg:String){
        dialog = SpotsDialog.Builder()
                .setContext(context)
                .setMessage(msg)
                .setCancelable(false)
                .setTheme(R.style.CustomProgressDialog)
                .build()

    }


    private fun displaySubmitting(state: Boolean) {
        if (state) dialog.show() else dialog.dismiss()

    }

}