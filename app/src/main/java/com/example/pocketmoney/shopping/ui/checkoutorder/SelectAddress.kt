package com.example.pocketmoney.shopping.ui.checkoutorder

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSelectAddressBinding
import com.example.pocketmoney.mlm.adapters.SelectAddressAdapter
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.ui.AddNewAddress
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.OtherEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAddress : BaseFragment<FragmentSelectAddressBinding>(FragmentSelectAddressBinding::inflate),
    SelectAddressAdapter.SelectAddressInterface {

    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    private lateinit var selectAddressAdapter:SelectAddressAdapter

    private lateinit var userID: String

    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onResume() {
        super.onResume()
        viewModel.setActiveStep(0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == RESULT_OK){
                    showToast(data!!.getStringExtra("Message")!!)
                }
                else{
                    showToast("Cancelled !!")
                }

            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvData()
        binding.btnAddNewAddress.setOnClickListener {
            val intent = Intent(requireActivity(),AddNewAddress::class.java)
            intent.putExtra("ACTION",OtherEnum.ADD)
            intent.putExtra("ID",0)
            resultLauncher.launch(intent)

        }
    }

    private fun setupRvData(){
        selectAddressAdapter = SelectAddressAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = selectAddressAdapter
        }
    }
    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner,{
            userID = it
            viewModel.getCustomerAddressList(userID)
        })
        viewModel.customerAddressList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        it[0].isSelected=true
                        viewModel.setSelectedAddress(it[0])
                        selectAddressAdapter.setCustomerAddressList(it)
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

    }

    override fun onAddressSelect(item: ModelAddress) {
        viewModel.setSelectedAddress(item)
    }

    override fun onEditClick(item: ModelAddress) {
        val intent = Intent(requireActivity(),AddNewAddress::class.java)
        intent.putExtra("ACTION",OtherEnum.EDIT)
        intent.putExtra("ID",item.AddressID)
        resultLauncher.launch(intent)

    }

    override fun onRemoveClick(item: ModelAddress) {

    }

}