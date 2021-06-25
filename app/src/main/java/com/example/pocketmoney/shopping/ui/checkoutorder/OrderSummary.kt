package com.example.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.FragmentOrderSummaryBinding
import com.example.pocketmoney.shopping.adapters.CartItemListAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.ui.BuyProduct
import com.example.pocketmoney.shopping.ui.CheckoutOrderInterface
import com.example.pocketmoney.shopping.viewmodel.AddressViewModel
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.ModelOrderAmountSummary
import com.example.pocketmoney.utils.ProgressBarHandler
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSummary : Fragment(), CartItemListAdapter.CartItemListAdapterListener {

    //UI
    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge:Double=0.0
    private var source: ShoppingEnum? = null
    private val args: OrderSummaryArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBarHandler = ProgressBarHandler(requireActivity())
        selectedAddressId = args.addressId
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkoutOrderInterface = requireActivity() as CheckoutOrder
        checkoutOrderInterface.updateCheckOutStepStatus(1)
        setUpCartItemRecyclerView()
        subscribeObservers()

    }

    private fun subscribeObservers() {

        shoppingAuthViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            addressViewModel.getAddressDetails(selectedAddressId, userID)
            addressViewModel.getShippingCharge(selectedAddressId,userID)

        })

        addressViewModel.addressDetail.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        createAddressView(it)
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
                    cartItemListAdapter.setCartItemList(dataState.data)
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

        cartViewModel.cartItemQuantity.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<Int> -> {
                    displayLoading(false)
                    cartViewModel.getCartItems(userID)
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

    private fun setUpCartItemRecyclerView() {
        cartItemListAdapter = CartItemListAdapter(this)
        binding.apply {
            rvCartItemList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = cartItemListAdapter
            }
        }
    }

    private fun populateValues(cartList:List<CartModel>,shippingCharge:Double){
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

        binding.orderAmountSummary.setAmountSummary(
                ModelOrderAmountSummary(
                        itemQuantity,
                        productOldPrice,
                        saving,
                        totalPrice,
                        shippingCharge,
                        extraDiscount,
                        tax,
                        grandTotal

                )
        )
        binding.orderAmountSummary.setVisibilityStatus(1)
        checkoutOrderInterface.setPriceDetailNAction(grandTotal)

    }

    fun createAddressView(modelAddress: ModelAddress) {
        binding.apply {
            layoutSelectedAddress.root.visibility = View.VISIBLE
            layoutSelectedAddress.apply {

                tvName.text = modelAddress.Name
                val sbAddress = StringBuilder()

                sbAddress.append(modelAddress.Address1).append(", ")
                sbAddress.append(modelAddress.Street).append(", ")
                sbAddress.append(modelAddress.Locality).append(", ")
                sbAddress.append(modelAddress.CityName).append(" - ")
                sbAddress.append(modelAddress.PostalCode).append(", ")
                sbAddress.append(modelAddress.StateName).append(", ")
                sbAddress.append(modelAddress.CountryName)

                tvAddress.text = sbAddress.toString()

                tvMobileNumber.text = modelAddress.MobileNo

                if (modelAddress.isSelected == true) {
                    btnDeliverHere.visibility = View.VISIBLE
                } else btnDeliverHere.visibility = View.GONE

            }
        }
    }

    fun initialUIStates() {
        binding.layoutSelectedAddress.root.visibility = View.VISIBLE
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

    override fun onItemQuantityIncrease(itemID: Int) {
        cartViewModel.changeCartItemQuantity(1,itemID, userID)
    }

    override fun onItemQuantityDecrease(itemID: Int) {
        cartViewModel.changeCartItemQuantity(0,itemID, userID)
    }

    override fun onItemDelete(itemID: Int) {
        cartViewModel.changeCartItemQuantity(-1,itemID, userID)
    }

    override fun onItemClick(itemID: Int) {
        val intent = Intent(requireActivity(), BuyProduct::class.java)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)
    }



}