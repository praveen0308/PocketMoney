package com.jmm.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.manage_address.AddNewAddress
import com.jmm.manage_address.SelectAddressAdapter
import com.jmm.manage_address.databinding.FragmentSelectShippingAddressBinding
import com.jmm.model.myEnums.OtherEnum
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.util.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectShippingAddress : BaseBottomSheetDialogFragment<FragmentSelectShippingAddressBinding>(FragmentSelectShippingAddressBinding::inflate),
    SelectAddressAdapter.SelectAddressInterface {

    private val viewModel by activityViewModels<CheckoutViewModel>()


    private lateinit var selectAddressAdapter: SelectAddressAdapter




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
        setupRvData()
        selectAddressAdapter.setCustomerAddressList(viewModel.shippingAddressList)
        binding.btnDeliverHere.setOnClickListener {
            dismiss()
        }

    }
    override fun subscribeObservers() {


    }
    private fun setupRvData() {
        selectAddressAdapter = SelectAddressAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                layoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = selectAddressAdapter
        }
    }

    override fun onAddressSelect(item: ModelAddress) {
        viewModel.selectedAddressId.postValue(item.AddressID)
    }

    override fun onEditClick(item: ModelAddress) {
        val intent = Intent(requireActivity(), AddNewAddress::class.java)
        intent.putExtra("ACTION", OtherEnum.EDIT)
        intent.putExtra("ID", item.AddressID)
        resultLauncher.launch(intent)
    }

    override fun onRemoveClick(item: ModelAddress) {

    }

}