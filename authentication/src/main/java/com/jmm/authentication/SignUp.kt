package com.jmm.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.jmm.authentication.databinding.ActivitySignUpBinding
import com.jmm.model.ModelCustomerDetail
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.CustomValidator
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class SignUp : BaseActivity<ActivitySignUpBinding>(ActivitySignUpBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private lateinit var dialog: android.app.AlertDialog

    private lateinit var validator: CustomValidator

    // ViewModel
    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var customerDetail: ModelCustomerDetail

    private var sponsorId = ""
    private var sponsorName = ""

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

                            viewModel.checkAccountAlreadyExist(etMobileNumber.text.toString())

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
            if (it!!.length == 10) viewModel.getSponsorName(it.toString())
        }
    }

    private fun initialUiState() {
        createProgressDialog("Verifying....")


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
        viewModel.userSponsorId.observe(this) {
            sponsorId = it
            binding.etSponsorId.setText(sponsorId)
        }

        viewModel.userSponsorName.observe(this) {
            sponsorName = it
            binding.etSponsorName.setText(sponsorName)
        }

        viewModel.pageState.observe(this) { state ->
            displayLoading(false)
            dialog.hide()
            when (state) {
                is RegisterPageState.AccountStatus -> {
                    if (state.status) {
                        if (customerDetail != null) viewModel.registerUser(customerDetail)

                    } else {
                        Toast.makeText(this, "User Already Exist", Toast.LENGTH_SHORT).show()
                        displaySubmitting(false)
                    }
                }
                is RegisterPageState.Error -> displayError(state.msg)
                RegisterPageState.Idle -> {}
                RegisterPageState.Loading -> displayLoading(true)
                RegisterPageState.MessageSent -> {
                    showToast("Registered successfully!!!")
                    startActivity(Intent(this,SignIn::class.java))
                    finish()
                }
                is RegisterPageState.OnRegistrationComplete -> {
                    viewModel.sendRegistrationSms(
                        state.customerDetail.Mobile!!,
                        state.customerDetail.Mobile!!,
                        state.customerDetail.Password!!
                    )
                }
                is RegisterPageState.Processing -> {
                    dialog.setMessage(state.msg)
                }
                is RegisterPageState.ReceivedSponsorName -> {
                    if (state.name.isEmpty())
                        binding.tilSponsorId.error = "Sponsor id does not exit !!!"
                    else
                        binding.tilSponsorId.error = null
                    binding.etSponsorName.setText(state.name)
                }
            }

        }

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