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
import com.example.pocketmoney.shopping.adapters.OrderTrackingAdapter
import com.example.pocketmoney.shopping.model.OrderTrackingStep
import com.example.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import com.example.pocketmoney.shopping.model.orderModule.OrderItemModel
import com.example.pocketmoney.shopping.model.orderModule.Shipping
import com.example.pocketmoney.shopping.viewmodel.OrderViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetails :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {


    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val orderViewModel by viewModels<OrderViewModel>()

    // Adapters
    private lateinit var orderItemListAdapter: OrderItemListAdapter
    private lateinit var orderTrackingAdapter: OrderTrackingAdapter

    // Variable
    private lateinit var userID: String
    private lateinit var modelOrderDetail: ModelOrderDetails
    private val args by navArgs<OrderDetailsArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.getOrderDetails(args.orderNumber)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUIStates()
        binding.toolbarOrderDetails.setApplicationToolbarListener(this)
        setupRvOrderItemList()

    }


    override fun subscribeObservers() {

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

    private fun populateViews(orderDetail: ModelOrderDetails) {

        binding.apply {
            llOrderDetailsContent.visibility = View.VISIBLE
            orderDetailHeader.apply {
                tvOrderNumber.text = orderDetail.OrderListModel.OrderNumber
                tvOrderDate.text = convertISOTimeToDate(orderDetail.OrderListModel.OrderDate)

            }
            generateOrderTracking(orderDetail)
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

    private fun getTotalItemQuantity(itemList: List<OrderItemModel>): Int {
        var qty = 0
        for (item in itemList) {
            qty += item.Quantity
        }

        return qty
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }

    private fun generateOrderTracking(orderDetail: ModelOrderDetails) {
        if (orderDetail.ShippingDetail.isNotEmpty()) {
            binding.orderDetailHeader.tvTrackingNumber.text =
                orderDetail.ShippingDetail[0].TrackingNumber
            val statusId = orderDetail.ShippingDetail[0].ShippingStatusId
            val tracking = mutableListOf<OrderTrackingStep>()

            var step = 1
            tracking.add(
                OrderTrackingStep(
                    "Order Received",
                    convertISOTimeToDate(orderDetail.OrderListModel.OrderDate),
                    convertISOTimeToAny(orderDetail.OrderListModel.OrderDate, SDF_dM).toString(),
                    step == statusId!!
                )
            )
            step++

            tracking.add(
                OrderTrackingStep(
                    "Shipped",
                    "Shipped On ${convertTimeStampToDate(orderDetail.ShippingDetail[0].ShippedOn.toString())}",
                    "",
                    step == statusId
                )
            )

            step++
            tracking.add(
                OrderTrackingStep(
                    "On the way",
                    "",
                    "",
                    step == statusId
                )
            )
            step++
            tracking.add(
                OrderTrackingStep(
                    "Delivered",
                    "Expected delivery ${convertISOTimeToDate(orderDetail.ShippingDetail[0].DeliveredOn!!)}",
                    convertISOTimeToAny(orderDetail.OrderListModel.OrderDate, SDF_dM).toString(),
                    step == statusId
                )
            )
            orderTrackingAdapter = OrderTrackingAdapter(tracking)
            binding.orderTrackingLayout.setAdapter(orderTrackingAdapter)
        }
    }


}