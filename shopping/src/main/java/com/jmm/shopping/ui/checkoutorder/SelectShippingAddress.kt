package com.jmm.shopping.ui.checkoutorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.model.myEnums.OtherEnum
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.shopping.adapters.SelectAddressAdapter
import com.jmm.shopping.databinding.FragmentSelectShippingAddressBinding
import com.jmm.shopping.ui.AddNewAddress
import com.jmm.shopping.viewmodel.CheckoutOrderViewModel
import com.jmm.util.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectShippingAddress : BaseBottomSheetDialogFragment<FragmentSelectShippingAddressBinding>(FragmentSelectShippingAddressBinding::inflate),
    SelectAddressAdapter.SelectAddressInterface {

    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository
    private lateinit var selectAddressAdapter: SelectAddressAdapter

    private var selectedAddressId = 0


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
        selectedAddressId = viewModel.selectedAddressId.value!!
        selectAddressAdapter.setCustomerAddressList(viewModel.shippingAddressList)
        binding.btnDeliverHere.setOnClickListener {
            viewModel.selectedAddressId.postValue(selectedAddressId)
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
        selectedAddressId = item.AddressID
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