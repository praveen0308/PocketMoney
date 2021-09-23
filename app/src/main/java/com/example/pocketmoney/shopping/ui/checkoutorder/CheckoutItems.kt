package com.example.pocketmoney.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentCheckoutItemsBinding
import com.example.pocketmoney.shopping.adapters.CartItemListAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.shopping.ui.BuyProduct
import com.example.pocketmoney.shopping.viewmodel.CheckoutOrderViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.ModelOrderAmountSummary
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutItems : BaseFragment<FragmentCheckoutItemsBinding>(FragmentCheckoutItemsBinding::inflate),
    CartItemListAdapter.CartItemListAdapterListener {


    //ViewModels
    private val viewModel by activityViewModels<CheckoutOrderViewModel>()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter

    private lateinit var userID: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCartItemRecyclerView()
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it

        })


        viewModel.selectedAddressId.observe(viewLifecycleOwner,{addressId->
            val address = viewModel.shippingAddressList.find { it.AddressID ==addressId }
            address?.let {
                viewModel.getShippingCharge(it.AddressID,userID)
            }

        })

        viewModel.shippingCharge.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.mShippingCharge=it
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
                        populateValues(it)
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


    private fun populateValues(cartList:List<CartModel>){
        binding.orderAmountSummary.visibility=View.VISIBLE
        viewModel.itemQuantity =0
        viewModel.productOldPrice =0.0
        viewModel.totalAmount =0.0

        for (item in cartList){
            viewModel.itemQuantity+=item.Quantity
            viewModel.productOldPrice+=item.Old_Price*item.Quantity
            viewModel.totalAmount+=item.Price*item.Quantity
        }

        viewModel.saving = viewModel.productOldPrice - viewModel.totalAmount
        viewModel.grandTotal = (viewModel.totalAmount + viewModel.mShippingCharge + viewModel.tax) - checkoutRepository.discountAmount

        binding.orderAmountSummary.setAmountSummary(
            ModelOrderAmountSummary(
                viewModel.itemQuantity,
                viewModel.productOldPrice,
                viewModel.saving,
                viewModel.totalAmount,
                viewModel.mShippingCharge,
                checkoutRepository.discountAmount,
                viewModel.tax,
                viewModel.grandTotal

            )
        )
        binding.orderAmountSummary.setVisibilityStatus(1)
        viewModel.amountPayable.postValue(viewModel.grandTotal)

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