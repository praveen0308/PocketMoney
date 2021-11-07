package com.sampurna.pocketmoney.shopping.ui.checkoutorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentManageAddressBinding
import com.sampurna.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.sampurna.pocketmoney.shopping.adapters.AddressAdapter
import com.sampurna.pocketmoney.shopping.model.ModelAddress
import com.sampurna.pocketmoney.shopping.ui.AddNewAddress
import com.sampurna.pocketmoney.shopping.viewmodel.AddressViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
import com.sampurna.pocketmoney.utils.myEnums.OtherEnum
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Address : BaseFragment<FragmentManageAddressBinding>(FragmentManageAddressBinding::inflate), AddressAdapter.AddressAdapterListener, ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel: AddressViewModel by viewModels()


    // Adapters
    private lateinit var addressAdapter: AddressAdapter

    // Variable
    private lateinit var userID: String

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        (activity as MainDashboard).toggleBottomNav(false)
    }

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

        binding.toolbarAddress.setApplicationToolbarListener(this)

        setUpRecyclerview()
        binding.fabAddNewAddress.setOnClickListener {
            val intent = Intent(requireActivity(), AddNewAddress::class.java)
            intent.putExtra("ACTION", OtherEnum.ADD)
            intent.putExtra("ID", 0)
            resultLauncher.launch(intent)
        }
    }

    override fun subscribeObservers() {

        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it
            viewModel.getCustomerAddressList(userID)
        })

        viewModel.customerAddressList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isNullOrEmpty()){
                            binding.emptyView.isVisible=true
                            binding.rvAddressList.isVisible=false
                        }
                        else{
                            binding.emptyView.isVisible=false
                            binding.rvAddressList.isVisible=true
                            addressAdapter.setAddressList(it)
                        }
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

    private fun setUpRecyclerview(){
        addressAdapter = AddressAdapter(this)
        binding.rvAddressList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
    }

    override fun onActionButtonClick(modelAddress: ModelAddress) {

    }

    override fun onEditButtonClick(modelAddress: ModelAddress) {
        val intent = Intent(requireActivity(), AddNewAddress::class.java)
        intent.putExtra("ACTION", OtherEnum.EDIT)
        intent.putExtra("ID", modelAddress.AddressID)
        resultLauncher.launch(intent)
    }

    override fun onRemoveButtonClick(modelAddress: ModelAddress) {

    }

    override fun onToolbarNavClick() {
        findNavController().navigateUp()

    }

    override fun onMenuClick() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarHandler.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainDashboard).toggleBottomNav(true)
    }


}