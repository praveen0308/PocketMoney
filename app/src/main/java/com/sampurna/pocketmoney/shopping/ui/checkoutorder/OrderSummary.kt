package com.sampurna.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentOrderSummaryBinding
import com.sampurna.pocketmoney.shopping.adapters.CartItemListAdapter
import com.sampurna.pocketmoney.shopping.model.CartModel
import com.sampurna.pocketmoney.shopping.model.ModelAddress
import com.sampurna.pocketmoney.shopping.repository.CheckoutRepository
import com.sampurna.pocketmoney.shopping.ui.BuyProduct
import com.sampurna.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.ModelOrderAmountSummary
import com.sampurna.pocketmoney.utils.Status
import com.sampurna.pocketmoney.utils.myEnums.ShoppingEnum
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderSummary : BaseFragment<FragmentOrderSummaryBinding>(FragmentOrderSummaryBinding::inflate), CartItemListAdapter.CartItemListAdapterListener {


    //ViewModels
    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter
//    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge:Double=0.0
    private var source: ShoppingEnum? = null
    private val args: OrderSummaryArgs by navArgs()

    override fun onResume() {
        super.onResume()
        viewModel.setActiveStep(1)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setActiveStep(1)
//        checkoutOrderInterface = requireActivity() as CheckoutOrder
//        checkoutOrderInterface.updateCheckOutStepStatus(1)
        setUpCartItemRecyclerView()


    }

    override fun subscribeObservers() {

        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it
//            addressViewModel.getAddressDetails(selectedAddressId, userID)
//            addressViewModel.getShippingCharge(selectedAddressId,userID)

        })
        viewModel.selectedAddress.observe(viewLifecycleOwner,{
            createAddressView(it)
            selectedAddressId = it.AddressID!!
            viewModel.getShippingCharge(selectedAddressId,userID)

        })
//        addressViewModel.addressDetail.observe(viewLifecycleOwner, { _result ->
//            when (_result.status) {
//                Status.SUCCESS -> {
//                    _result._data?.let {
//                        createAddressView(it)
//                        addressViewModel.getShippingCharge(selectedAddressId,userID)
//                    }
//                    displayLoading(false)
//                }
//                Status.LOADING -> {
//                    displayLoading(true)
//                }
//                Status.ERROR -> {
//                    displayLoading(false)
//                    _result.message?.let {
//                        displayError(it)
//                    }
//                }
//            }
//        })

        viewModel.shippingCharge.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        shippingCharge=it
//                        checkoutRepository.shippingCharge = it
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
                        cartItemListAdapter.setCartItemList(it)
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

        viewModel.cartItemQuantity.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
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
        viewModel.setPayableAmount(grandTotal)
//        checkoutOrderInterface.setPriceDetailNAction(grandTotal)

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

                btnChangeAddress.setOnClickListener {
                   findNavController().navigate(OrderSummaryDirections.actionOrderSummaryToSelectAddress())
                }
            }
        }
    }

    fun initialUIStates() {
        binding.layoutSelectedAddress.root.visibility = View.VISIBLE
    }

    override fun onItemQuantityIncrease(itemID: Int) {
        viewModel.changeCartItemQuantity(1,itemID, userID)
    }

    override fun onItemQuantityDecrease(itemID: Int) {
        viewModel.changeCartItemQuantity(0,itemID, userID)
    }

    override fun onItemDelete(itemID: Int) {
        viewModel.changeCartItemQuantity(-1,itemID, userID)
    }

    override fun onItemClick(itemID: Int) {
        val intent = Intent(requireActivity(), BuyProduct::class.java)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)
    }



}