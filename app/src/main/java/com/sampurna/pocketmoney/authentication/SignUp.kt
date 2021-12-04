package com.sampurna.pocketmoney.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.ActivitySignUpBinding
import com.sampurna.pocketmoney.mlm.model.ModelCustomerDetail
import com.sampurna.pocketmoney.mlm.viewmodel.AccountViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import com.sampurna.pocketmoney.utils.CustomValidator
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class SignUp : BaseActivity<ActivitySignUpBinding>(ActivitySignUpBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private lateinit var dialog: android.app.AlertDialog

    private lateinit var validator: CustomValidator

    // ViewModel
    private val accountViewModel: AccountViewModel by viewModels()

    private lateinit var customerDetail: ModelCustomerDetail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarSignUp.setApplicationToolbarListener(this)
        initialUiState()
        initiateFieldsValidation()
        binding.btnRegister.setOnClickListener {
            binding.apply {
                if (etSponsorId.text.toString().length == 10 && tilSponsorId.error == null) {
                    if (validator.validateName(tilFullName, etFullName) &&
                        validator.validateEmail(tilEmail, etEmail) &&
                        validator.validateMobileNo(tilMobileNumber, etMobileNumber)
                        && validator.validatePincode(tilPincode, etPincode)
                    ) {
                        if (cbTncAgreement.isChecked) {
                            customerDetail = ModelCustomerDetail(
                                SponsorID = etSponsorId.text.toString(),
                                SponsorName = etSponsorName.text.toString(),
                                FullName = etFullName.text.toString(),
                                EmailID = etEmail.text.toString(),
                                Mobile = etMobileNumber.text.toString(),
                                PinNo = etPincode.text.toString(),
                                Address1 = "",
                                Address2 = "",
                            )

                            accountViewModel.checkAccountAlreadyExist(etMobileNumber.text.toString())

                        } else {
                            cbTncAgreement.requestFocus()
                        }
                    }
                } else {
                    etSponsorId.requestFocus()
                }
            }
        }
        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }



        binding.etSponsorId.doAfterTextChanged {
            if (it!!.length == 10) accountViewModel.getSponsorName(it.toString())
        }
    }

    private fun initialUiState() {
        createProgressDialog("Verifying....")
        binding.apply {
            etSponsorId.setText(getString(R.string.default_sponsor_id))

        }

    }

    private fun initiateFieldsValidation() {
        validator =
            CustomValidator(this)
        binding.apply {
            etFullName.doAfterTextChanged { validator.validateName(tilFullName, etFullName) }
            etEmail.doAfterTextChanged { validator.validateEmail(tilEmail, etEmail) }
            etMobileNumber.doAfterTextChanged {
                validator.validateMobileNo(
                    tilMobileNumber,
                    etMobileNumber
                )
            }
            etPincode.doAfterTextChanged { validator.validatePincode(tilPincode, etPincode) }

        }

    }

    override fun subscribeObservers() {

        accountViewModel.sponsorName.observe(this, { _result ->
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

        accountViewModel.isAccountDuplicate.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            if (customerDetail != null) accountViewModel.registerUser(customerDetail)

                        } else {
                            showToast("User Already Exist")
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


        accountViewModel.isSuccessfullyRegistered.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
//                            accountViewModel.sendRegistrationSms(
//                                binding.etMobileNumber.text.toString()
//                            )
                            showToast("Registered successfully!!!")
//                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                            dismiss()
                        } else {
                            displayError("Registration failed !!!")
                            displaySubmitting(false)
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

    private fun createProgressDialog(msg: String) {
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(msg)
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialog)
            .build()

    }


    private fun displaySubmitting(state: Boolean) {
        if (state) dialog.show() else dialog.dismiss()

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }


}