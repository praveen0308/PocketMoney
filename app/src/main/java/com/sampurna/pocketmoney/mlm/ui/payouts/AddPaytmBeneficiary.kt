package com.sampurna.pocketmoney.mlm.ui.payouts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.brsap.dialog_builder.DialogType
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentAddPaytmBeneficiaryBinding
import com.sampurna.pocketmoney.mlm.adapters.UpiSelectorAdapter
import com.sampurna.pocketmoney.mlm.model.UPIModel
import com.sampurna.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPaytmBeneficiary : BaseBottomSheetDialogFragment<FragmentAddPaytmBeneficiaryBinding>(
    FragmentAddPaytmBeneficiaryBinding::inflate
),
    UpiSelectorAdapter.UpiSelectorInterface {
    private val viewModel by activityViewModels<PayoutViewModel>()
    private var userId: String = ""
    private var isAddedBeneficiary = false

    private lateinit var upiSelectorAdapter: UpiSelectorAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK) {
//                    showToast(data!!.getStringExtra("Message")!!)
                    data?.let {
                        binding.apply {
                            etUpiId.setText(it.getStringExtra("upiId"))
                            etCustomerName.setText(it.getStringExtra("name"))
                        }
                    }

                } else {
                    showToast("Cancelled !!")
                }

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.payoutType.value == 2) {
            binding.btnQr.isVisible = true
            binding.etUpiId.hint = "Enter UPI Id"
            binding.etUpiId.inputType = EditorInfo.TYPE_CLASS_TEXT
            setupRvUpis()
        } else {
            binding.btnQr.isVisible = false
            binding.etUpiId.hint = "Paytm Number"
            binding.etUpiId.inputType = EditorInfo.TYPE_CLASS_NUMBER
        }

        binding.btnQr.setOnClickListener {
            val intent = Intent(requireActivity(), ScanQR::class.java)
            resultLauncher.launch(intent)

        }
        binding.btnSubmit.setButtonClick {
            if (userId.isEmpty()) {
                checkAuthorization()
            } else {
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

    private fun setupRvUpis() {
        upiSelectorAdapter = UpiSelectorAdapter(this)
        binding.rvUpis.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upiSelectorAdapter
        }

        val upiList = mutableListOf<UPIModel>()
        upiList.add(UPIModel("@ybl", R.drawable.ic_phone_pe))
        upiList.add(UPIModel("@paytm", R.drawable.ic_paytm))
        upiList.add(UPIModel("@upi", R.drawable.ic_bhim))
        upiList.add(UPIModel("@okaxis", R.drawable.ic_google_pay))
        upiSelectorAdapter.setUPIModelList(upiList)
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userId = it
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

    override fun onItemClick(item: UPIModel) {
        var str = binding.etUpiId.text.toString()
        if (str.contains("@")) {
            str = str.substring(0, str.indexOf("@"))
        }



        binding.etUpiId.setText("${str}${item.title}")
    }
}