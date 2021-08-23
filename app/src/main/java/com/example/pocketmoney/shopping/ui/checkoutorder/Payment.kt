package com.example.pocketmoney.shopping.ui.checkoutorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPaymentBinding
import com.example.pocketmoney.shopping.adapters.PaymentMethodAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Payment : BaseFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface {

    //ViewModels
    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    // Adapters

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge: Double = 0.0
    private var source: ShoppingEnum? = null
    private lateinit var modelOrderAmountSummary: ModelOrderAmountSummary
    private val args: PaymentArgs by navArgs()
    private var paymentMode : PaymentEnum = PaymentEnum.WALLET

    override fun onResume() {
        super.onResume()
        viewModel.setActiveStep(2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setActiveStep(2)
        setupRvPaymentMethods()

    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter = PaymentMethodAdapter(getPaymentMethods(),this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }

    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.WALLET, "Wallet", R.drawable.ic_logo,true))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.GATEWAY, "Online Payment", R.drawable.ic_paytm_logo))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.COD, "Cash On Delivery", R.drawable.ic_baseline_location_on_24))


        return paymentMethods
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it

        })
        viewModel.selectedAddress.observe(viewLifecycleOwner, {
            selectedAddressId = it.AddressID!!
            viewModel.getShippingCharge(selectedAddressId, userID)

        })
        viewModel.shippingCharge.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        shippingCharge = it
                        viewModel.getCartItems(userID)
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
        viewModel.cartItems.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateValues(it, shippingCharge)
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

    /*    fun getCustomerOrder():CustomerOrder{
            val customerOrder = CustomerOrder(
                    UserID= userID,
                    ShippingAddressId = selectedAddressId,
                    Shipping = shippingCharge,
                    Total = modelOrderAmountSummary.totalPrice,
                    Discount = modelOrderAmountSummary.extraDiscount,
                    GrandTotal = modelOrderAmountSummary.grandTotal,
                    PaymentMethod = "Testing",
                    Payment = modelOrderAmountSummary.grandTotal

            )
            return customerOrder
        }*/
    private fun populateValues(cartList: List<CartModel>, shippingCharge: Double) {
        binding.orderAmountSummary.visibility = View.VISIBLE

        var itemQuantity: Int = 0
        var productOldPrice: Double = 0.0
        var saving: Double = 0.0
        var totalPrice: Double = 0.0
        var tax: Double = 0.0
        var grandTotal: Double = 0.0
        var extraDiscount: Double = 0.0

        for (item in cartList) {
            itemQuantity += item.Quantity
            productOldPrice += item.Old_Price * item.Quantity
            totalPrice += item.Price * item.Quantity
        }

        saving = productOldPrice - totalPrice

        grandTotal = totalPrice + shippingCharge + tax - extraDiscount
        val orderAmountSummary = ModelOrderAmountSummary(
            itemQuantity,
            productOldPrice,
            saving,
            totalPrice,
            shippingCharge,
            extraDiscount,
            tax,
            grandTotal
        )
        binding.orderAmountSummary.setAmountSummary(orderAmountSummary)
        modelOrderAmountSummary = orderAmountSummary
        binding.orderAmountSummary.setVisibilityStatus(1)
        viewModel.setPayableAmount(grandTotal)


    }

    override fun onPaymentModeSelected(item: ModelPaymentMethod) {
        paymentMode = item.method
        viewModel.paymentMethod.postValue(item.method)
    }

}