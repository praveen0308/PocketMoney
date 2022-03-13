package com.jmm.shopping.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.shopping.adapters.CartItemListAdapter
import com.jmm.shopping.databinding.ActivityYourCartBinding
import com.jmm.shopping.ui.checkoutorder.NewCheckout
import com.jmm.shopping.viewmodel.CartPageState
import com.jmm.shopping.viewmodel.CartViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class YourCart : BaseActivity<ActivityYourCartBinding>(ActivityYourCartBinding::inflate),
    CartItemListAdapter.CartItemListAdapterListener, ApplicationToolbar.ApplicationToolbarListener {

    //Ui

    // ViewModels
    private val viewModel: CartViewModel by viewModels()

    @Inject
    lateinit var checkoutRepository: CheckoutRepository

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter

    // Variable
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialUiState()

        binding.toolbarYourCart.setApplicationToolbarListener(this)
        setUpCartItemRecyclerView()

        binding.btnCheckout.setOnClickListener {
//            startActivity(Intent(this, CheckoutOrder::class.java))
            startActivity(Intent(this, NewCheckout::class.java))
        }

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
                CartPageState.EmptyCart ->{
                    binding.root.children.forEach {
                        it.isVisible = false
                    }
                    binding.emptyView.isVisible =true
                }
                is CartPageState.Error -> {
                    showToast(state.msg)
                }
                CartPageState.Idle -> {

                }
                CartPageState.Loading ->displayLoading(true)
                is CartPageState.ReceivedCartItems -> {
                    cartItemListAdapter.setCartItemList(state.cartItems)
                }
            }

        }

    }

    private fun initialUiState() {
        binding.btnCheckout.visibility = View.GONE
        binding.emptyView.visibility = View.GONE

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

   /* private fun populateValues(cartList: List<CartModel>) {

        binding.layoutCartSummary.visibility = View.VISIBLE
        var itemQuantity = 0
        var productOldPrice = 0.0
        var saving = 0.0
        var totalPrice = 0.0
        var discount = 0.0

        for (item in cartList) {
            itemQuantity += item.Quantity
            productOldPrice += item.Old_Price * item.Quantity
            totalPrice += item.Price * item.Quantity
        }

        saving = productOldPrice - totalPrice

        if (!checkoutRepository.appliedCouponCode.value.isNullOrEmpty()){
            if (checkoutRepository.isFixed.value==true){
                discount =checkoutRepository.appliedDiscount.value!!
            }else{
                discount = (checkoutRepository.appliedDiscount.value!!*totalPrice)/100

            }
        }


        binding.layoutCartSummary.setAmountSummary(
            ModelOrderAmountSummary(
                itemQuantity,
                productOldPrice,
                saving,
                totalPrice,
                extraDiscount = discount
            )
        )
        binding.layoutCartSummary.setVisibilityStatus(1)

    }*/

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
        val intent = Intent(this@YourCart, BuyProduct::class.java)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}