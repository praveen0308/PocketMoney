package com.example.pocketmoney.shopping.ui.customernavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.FragmentOrderDetailsBinding
import com.example.pocketmoney.shopping.adapters.OrderItemListAdapter
import com.example.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import com.example.pocketmoney.shopping.model.orderModule.OrderItemModel
import com.example.pocketmoney.shopping.viewmodel.OrderViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetails : Fragment(), ApplicationToolbar.ApplicationToolbarListener {

    //UI
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val orderViewModel by viewModels<OrderViewModel>()

    // Adapters
    private lateinit var orderItemListAdapter: OrderItemListAdapter


    // Variable
    private lateinit var userID: String
    private lateinit var modelOrderDetail:ModelOrderDetails
    private val args by navArgs<OrderDetailsArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())
        orderViewModel.getOrderDetails(args.orderNumber)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUIStates()
        binding.toolbarOrderDetails.setApplicationToolbarListener(this)
        setupRvOrderItemList()
        subscribeObservers()

    }


    private fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            orderViewModel.getOrderList(userID)

        })

        orderViewModel.orderDetails.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateViews(it)
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

    private fun setupRvOrderItemList() {
        orderItemListAdapter = OrderItemListAdapter()
        binding.apply {
            rvOrderItems.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = orderItemListAdapter
            }
        }
    }

    fun initialUIStates() {
        binding.llOrderDetailsContent.visibility = View.GONE
    }
    private fun populateViews(orderDetail:ModelOrderDetails){

        binding.apply {
            llOrderDetailsContent.visibility = View.VISIBLE
            orderDetailHeader.apply {
                tvOrderNumber.text = orderDetail.OrderListModel.OrderNumber
                tvOrderDate.text = convertISOTimeToDate(orderDetail.OrderListModel.OrderDate)
                tvTrackingNumber.text = "NA"
            }
            addressView.setModelAddress(orderDetail.ShippingDetailAddress)
            orderItemListAdapter.setOrderItemList(orderDetail.OrderItemListModel.toMutableList())
            tvNumberOfItems.text = "${getTotalItemQuantity(orderDetail.OrderItemListModel)} Items"
            orderAmountSummary.apply {
                tvNoOfItems.text = "Items (${getTotalItemQuantity(orderDetail.OrderItemListModel)})"
                tvProductTotal.text = "₹ ${orderDetail.OrderListModel.Total}"
                tvShippingCharges.text = "₹ ${orderDetail.OrderListModel.Shipping}"
                tvExtraDiscount.text = "₹ ${orderDetail.OrderListModel.Discount}"
                tvTax.text = "₹ ${orderDetail.OrderListModel.Tax}"
                tvTotalAmount.text = "₹ ${orderDetail.OrderListModel.GrandTotal}"
            }
        }
    }

    private fun getTotalItemQuantity(itemList: List<OrderItemModel>):Int{
        var qty = 0
        for (item in itemList){
            qty+=item.Quantity
        }

        return qty
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
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }


}