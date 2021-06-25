package com.example.pocketmoney.shopping.ui.customernavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentOrderListBinding
import com.example.pocketmoney.shopping.adapters.OrderListAdapter
import com.example.pocketmoney.shopping.model.OrderListItem
import com.example.pocketmoney.shopping.viewmodel.OrderViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderList : Fragment(), ApplicationToolbar.ApplicationToolbarListener, OrderListAdapter.OrderListAdapterInterface {

    //UI
    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val orderViewModel by viewModels<OrderViewModel>()

    // Adapters
    private lateinit var orderListAdapter: OrderListAdapter


    // Variable
    private lateinit var userID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarOrderList.setApplicationToolbarListener(this)
        setupRvOrderList()
        subscribeObservers()

    }
    private fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            orderViewModel.getOrderList(userID)

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

    fun initialUIStates() {
//        binding.layoutSelectedAddress.root.visibility = View.VISIBLE
    }

    private fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
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