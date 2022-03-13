package com.jmm.shopping.ui.checkoutorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.shopping.adapters.CartItemListAdapter
import com.jmm.shopping.databinding.FragmentCheckoutItemsBinding
import com.jmm.shopping.ui.BuyProduct
import com.jmm.shopping.viewmodel.CheckoutOrderViewModel
import com.jmm.util.BaseFragment
import com.jmm.util.Status
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
        viewModel.userId.observe(viewLifecycleOwner) {
            userID = it

        }


        viewModel.selectedAddressId.observe(viewLifecycleOwner) { addressId ->
            val address = viewModel.shippingAddressList.find { it.AddressID == addressId }
            address?.let {
                viewModel.getShippingCharge(it.AddressID, userID)
            }

        }

       /* checkoutRepository.appliedCouponCode.observe(viewLifecycleOwner) {
//            populateValues(viewModel.mCartItemList)
        }*/

        viewModel.shippingCharge.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.mShippingCharge = it
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
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        cartItemListAdapter.setCartItemList(it)
                        viewModel.mCartItemList.clear()
                        viewModel.mCartItemList.addAll(it)
//                        populateValues(viewModel.mCartItemList)
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
        }

        viewModel.cartItemQuantity.observe(viewLifecycleOwner) { _result ->
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
        }

    }


//    private fun populateValues(cartList:List<CartModel>){
//        binding.orderAmountSummary.visibility=View.VISIBLE
//        viewModel.itemQuantity =0
//        viewModel.productOldPrice =0.0
//        viewModel.totalAmount =0.0
//
//        for (item in cartList){
//            viewModel.itemQuantity+=item.Quantity
//            viewModel.productOldPrice+=item.Old_Price*item.Quantity
//            viewModel.totalAmount+=item.Price*item.Quantity
//        }
//
//        if (!checkoutRepository.appliedCouponCode.value.isNullOrEmpty()){
//            if (checkoutRepository.isFixed.value==true){
//                viewModel.discountAmount =checkoutRepository.appliedDiscount.value!!
//            }else{
//                viewModel.discountAmount = (checkoutRepository.appliedDiscount.value!!*viewModel.totalAmount)/100
//
//            }
//        }
//
//        viewModel.saving = viewModel.productOldPrice - viewModel.totalAmount
//        viewModel.grandTotal = (viewModel.totalAmount + viewModel.mShippingCharge + viewModel.tax) - viewModel.discountAmount
//
//        binding.orderAmountSummary.setAmountSummary(
//            ModelOrderAmountSummary(
//                viewModel.itemQuantity,
//                viewModel.productOldPrice,
//                viewModel.saving,
//                viewModel.totalAmount,
//                viewModel.mShippingCharge,
//                viewModel.discountAmount,
//                viewModel.tax,
//                viewModel.grandTotal
//
//            )
//        )
//        binding.orderAmountSummary.setVisibilityStatus(1)
//        viewModel.amountPayable.postValue(viewModel.grandTotal)
//
//
//    }
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