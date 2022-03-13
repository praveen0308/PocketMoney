package com.jmm.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.jmm.brsap.dialog_builder.DialogType
import com.jmm.checkout.databinding.FragmentCheckoutShippingAddressBinding
import com.jmm.core.utils.showActionDialog
import com.jmm.manage_address.AddNewAddress
import com.jmm.model.myEnums.OtherEnum
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutShippingAddress :
    BaseFragment<FragmentCheckoutShippingAddressBinding>(FragmentCheckoutShippingAddressBinding::inflate) {

    private val viewModel by activityViewModels<CheckoutViewModel>()

    private lateinit var userID: String

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK) {
                    showToast(data!!.getStringExtra("Message")!!)
                } else {
                    showToast("Cancelled !!")
                }

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChangeAddress.setOnClickListener {
            val bottomSheet = SelectShippingAddress()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        binding.btnAddNewAddress.setOnClickListener {
            val intent = Intent(requireActivity(), AddNewAddress::class.java)
            intent.putExtra("ACTION", OtherEnum.ADD)
            intent.putExtra("ID", 0)
            resultLauncher.launch(intent)
        }
    }

    override fun subscribeObservers() {
        viewModel.userID.observe(viewLifecycleOwner) {
            userID = it
            viewModel.getCustomerAddressList(userID)
        }
        viewModel.selectedAddressId.observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.layoutShippingAddress.isVisible = false
                binding.tvNoShippingAddress.isVisible = true
            } else {
                binding.tvNoShippingAddress.isVisible = false
                binding.layoutShippingAddress.isVisible = true
            }
        }
        viewModel.customerAddressList.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
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
        }

        viewModel.selectedAddressId.observe(viewLifecycleOwner) { addressId ->
            if (addressId != 0) {
                val address = viewModel.shippingAddressList.find { it.AddressID == addressId }
                address?.let {
                    createAddressView(it)
                }

            }

        }
        viewModel.pageState.observe(viewLifecycleOwner) { state ->
            when (state) {
                CheckoutPageState.NoShippingAddressAvailable -> {
                    showActionDialog(requireActivity(),
                        DialogType.INFORMATION,
                        "No address available.",
                        "Kindly add atleast one shipping address in your account to get order placed! ",
                        mListener = {
                            val intent = Intent(requireActivity(), AddNewAddress::class.java)
                            intent.putExtra("ACTION", OtherEnum.ADD)
                            intent.putExtra("ID", 0)
                            resultLauncher.launch(intent)
                        })
                }
            }

        }
    }

    private fun createAddressView(modelAddress: ModelAddress) {
        binding.apply {

            tvName.text = modelAddress.Name
            val sbAddress = StringBuilder()

            sbAddress.append(modelAddress.Address1).append(", ")
            sbAddress.append(modelAddress.Street).append(", ")
            sbAddress.append(modelAddress.Locality).append(", ")
            sbAddress.append(modelAddress.CityName).append(" - ")
            sbAddress.append(modelAddress.PostalCode).append(", ")
            sbAddress.append(modelAddress.StateName).append(", ")
            sbAddress.append(modelAddress.CountryName)

            tvAddress.text = sbAddress.toString()

            tvMobileNumber.text = modelAddress.MobileNo


        }
    }


}