package com.sampurna.pocketmoney.shopping.ui.customernavigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentOrderListBinding
import com.sampurna.pocketmoney.shopping.adapters.OrderListAdapter
import com.sampurna.pocketmoney.shopping.model.OrderListItem
import com.sampurna.pocketmoney.shopping.viewmodel.OrderViewModel
import com.sampurna.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.sampurna.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderList : BaseFragment<FragmentOrderListBinding>(FragmentOrderListBinding::inflate), ApplicationToolbar.ApplicationToolbarListener, OrderListAdapter.OrderListAdapterInterface {


    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val orderViewModel by viewModels<OrderViewModel>()

    // Adapters
    private lateinit var orderListAdapter: OrderListAdapter


    // Variable
    private var userID: String = ""



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarOrderList.setApplicationToolbarListener(this)
        setupRvOrderList()

    }
    override fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            if(userID.isEmpty()){
                checkAuthorization()
            }else{
                orderViewModel.getOrderList(userID)
            }


        })

        orderViewModel.orderList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        orderListAdapter.setOrderList(it.toMutableList())
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

    private fun setupRvOrderList() {
        orderListAdapter = OrderListAdapter(this)
        binding.apply {
            rvOrderList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = orderListAdapter
            }
        }
    }


    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {

    }

    override fun onOrderDetailClick(item: OrderListItem) {
        findNavController().navigate(R.id.action_orderList_to_orderDetails,OrderDetailsArgs(item.OrderNumber).toBundle())
    }
}