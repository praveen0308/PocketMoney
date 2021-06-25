package com.example.pocketmoney.shopping.ui.checkoutorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.ModelOrderAmountSummary
import com.example.pocketmoney.utils.ProgressBarHandler
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Payment : Fragment() {

    //UI
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()


    // Adapters
    private lateinit var masterPaymentMethodAdapter: MasterPaymentMethodAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge:Double=0.0
    private var source: ShoppingEnum? = null
    private lateinit var modelOrderAmountSummary:ModelOrderAmountSummary
    private val args: PaymentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedAddressId = args.selectedAddressId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvPaymentMethod()
        subscribeObservers()
        checkoutOrderInterface = requireActivity() as CheckoutOrder
        checkoutOrderInterface.updateCheckOutStepStatus(2)
        masterPaymentMethodAdapter.setPaymentCategoryList(getPaymentMethods())
    }

    private fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            addressViewModel.getShippingCharge(selectedAddressId,userID)
        })

        addressViewModel.shippingCharge.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        shippingCharge=it
                        cartViewModel.getCartItems(userID)
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


        cartViewModel.cartItems.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<CartModel>> -> {
                    displayLoading(false)
//                    cartItemListAdapter.setCartItemList(dataState.data)
                    populateValues(dataState.data,shippingCharge)
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
        checkoutOrderInterface.setPriceDetailNAction(grandTotal)


    }

    private fun displayLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }
}