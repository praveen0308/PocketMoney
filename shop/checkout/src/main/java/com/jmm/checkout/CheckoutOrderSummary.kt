package com.jmm.checkout

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.checkout.adapters.CartItemListAdapter
import com.jmm.checkout.databinding.FragmentCheckoutOrderSummaryBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutOrderSummary : BaseFragment<FragmentCheckoutOrderSummaryBinding>(FragmentCheckoutOrderSummaryBinding::inflate),
    CartItemListAdapter.CartItemListAdapterListener {

    private lateinit var cartItemListAdapter: CartItemListAdapter
    private val viewModel by activityViewModels<CheckoutViewModel>()
    private lateinit var userID: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUiState()

        setUpCartItemRecyclerView()
    }

    override fun subscribeObservers() {
        viewModel.userID.observe(this) {
            if (it.isNullOrEmpty()) {
                checkAuthorization()
            } else {
                userID = it
                viewModel.getCartItems(userID)
            }

        }
        viewModel.pageState.observe(this){state->
            displayLoading(false)
            when(state){
                CheckoutPageState.EmptyCart ->{
                    binding.root.children.forEach {
                        it.isVisible = false
                    }


                }
                is CheckoutPageState.Error -> {
                    showToast(state.msg)
                }
                CheckoutPageState.Idle -> {

                }
                CheckoutPageState.Loading ->displayLoading(true)
                is CheckoutPageState.ReceivedCartItems -> {
                    cartItemListAdapter.setCartItemList(state.cartItems)
                }
            }

        }

    }
    private fun initialUiState() {

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
        viewModel.changeCartItemQuantity(1, itemID, userID)
    }

    override fun onItemQuantityDecrease(itemID: Int) {
        viewModel.changeCartItemQuantity(0, itemID, userID)
    }

    override fun onItemDelete(itemID: Int) {
        viewModel.changeCartItemQuantity(-1, itemID, userID)
    }

    override fun onItemClick(itemID: Int) {
        /*  val intent = Intent(requireActivity(), BuyProduct::class.java)
          intent.putExtra("PRODUCT_ITEM_ID", itemID)
          startActivity(intent)*/

    }

}