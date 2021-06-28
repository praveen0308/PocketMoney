package com.example.pocketmoney.shopping.ui.checkoutorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.FragmentPaymentBinding
import com.example.pocketmoney.shopping.adapters.MasterPaymentMethodAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.model.ModelMasterPaymentMethod
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import com.example.pocketmoney.shopping.ui.CheckoutOrderInterface
import com.example.pocketmoney.shopping.viewmodel.AddressViewModel
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Payment : BaseFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate) {

    //ViewModels
    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    // Adapters
    private lateinit var masterPaymentMethodAdapter: MasterPaymentMethodAdapter
//    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge:Double=0.0
    private var source: ShoppingEnum? = null
    private lateinit var modelOrderAmountSummary:ModelOrderAmountSummary
    private val args: PaymentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvPaymentMethod()
//        checkoutOrderInterface = requireActivity() as CheckoutOrder
//        checkoutOrderInterface.updateCheckOutStepStatus(2)
        masterPaymentMethodAdapter.setPaymentCategoryList(getPaymentMethods())
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it
//            addressViewModel.getAddressDetails(selectedAddressId, userID)
//            addressViewModel.getShippingCharge(selectedAddressId,userID)

        })
        viewModel.selectedAddress.observe(viewLifecycleOwner,{
            selectedAddressId = it.AddressID!!
            viewModel.getShippingCharge(selectedAddressId,userID)

        })
        viewModel.shippingCharge.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        shippingCharge=it
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
                        populateValues(it,shippingCharge)
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

    private fun setupRvPaymentMethod() {
        masterPaymentMethodAdapter = MasterPaymentMethodAdapter()
        binding.apply {
            rvPaymentMethods.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = masterPaymentMethodAdapter
            }
        }
    }

    private fun getPaymentMethods():MutableList<ModelMasterPaymentMethod>{
        val paymentMethods= mutableListOf<ModelMasterPaymentMethod>()

        val methodList = mutableListOf<Any>()
        methodList.add(ModelPaymentMethod(PaymentEnum.PAYTM,"Paytm",true))
        methodList.add(ModelPaymentMethod(PaymentEnum.WALLET,"Wallet"))
        methodList.add(ModelPaymentMethod(PaymentEnum.PAYMENT_CARD,"Credit/Debit Card"))
        methodList.add(ModelPaymentMethod(PaymentEnum.NET_BANKING,"Net Banking"))
        methodList.add(ModelPaymentMethod(PaymentEnum.COD,"Cash On Delivery"))
        paymentMethods.add(ModelMasterPaymentMethod("Payment Methods",methodList))

        return paymentMethods
    }

    fun getCustomerOrder():CustomerOrder{
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
    }
    private fun populateValues(cartList:List<CartModel>, shippingCharge:Double){
        binding.orderAmountSummary.visibility=View.VISIBLE

        var itemQuantity:Int=0
        var productOldPrice:Double=0.0
        var saving:Double=0.0
        var totalPrice:Double=0.0
        var tax:Double=0.0
        var grandTotal:Double=0.0
        var extraDiscount:Double=0.0

        for (item in cartList){
            itemQuantity+=item.Quantity
            productOldPrice+=item.Old_Price*item.Quantity
            totalPrice+=item.Price*item.Quantity
        }

        saving = productOldPrice-totalPrice

        grandTotal = totalPrice+shippingCharge+tax-extraDiscount
        val orderAmountSummary =ModelOrderAmountSummary(
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

}