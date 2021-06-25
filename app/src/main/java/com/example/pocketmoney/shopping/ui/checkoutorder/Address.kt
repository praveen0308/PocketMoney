package com.example.pocketmoney.shopping.ui.checkoutorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAddressBinding
import com.example.pocketmoney.shopping.adapters.AddressAdapter
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.ui.CheckoutOrderInterface
import com.example.pocketmoney.shopping.viewmodel.AddressViewModel
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "source"

@AndroidEntryPoint
class Address : BaseFragment<FragmentAddressBinding>(FragmentAddressBinding::inflate), AddressAdapter.AddressAdapterListener, ApplicationToolbar.ApplicationToolbarListener {

    //UI

    //ViewModels
    private val viewModel by activityViewModels<CheckoutOrderViewModel>()
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()


    // Adapters
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var source:ShoppingEnum?=null
    private val args: AddressArgs by navArgs()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        source = arguments?.getSerializable("source") as ShoppingEnum

        binding.toolbarAddress.setApplicationToolbarListener(this)
        when(args.source){
            ShoppingEnum.CHECKOUT ->{
                binding.toolbarAddress.visibility = View.GONE
                checkoutOrderInterface = requireActivity() as CheckoutOrder
                checkoutOrderInterface.updateCheckOutStepStatus(0)
            }
            ShoppingEnum.MY_ACCOUNT->{
                binding.toolbarAddress.visibility = View.VISIBLE

            }
        }
        setUpRecyclerview()
        binding.fabAddNewAddress.setOnClickListener {
            findNavController().navigate(R.id.action_address_to_addAddress)
        }
    }

    override fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            addressViewModel.getCustomerAddressList(userID)
        })

        addressViewModel.customerAddressList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ModelAddress>> -> {
                    displayLoading(false)
                    dataState.data[0].isSelected=true
                    addressAdapter.setAddressList(dataState.data.toMutableList())
                }
                is DataState.Loading -> {
                    displayLoading(true)

                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayError(dataState.exception.message)
                }
            }
        })


    }

    private fun setUpRecyclerview(){
        addressAdapter = AddressAdapter(args.source,this)
        binding.rvAddressList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
    }

    override fun onActionButtonClick(modelAddress: ModelAddress) {
        checkoutOrderInterface.onDeliveryAddressSelected(modelAddress.AddressID!!)
    }

    override fun onEditButtonClick(modelAddress: ModelAddress) {

    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()

    }

    override fun onMenuClick() {

    }



}