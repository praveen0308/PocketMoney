package com.example.pocketmoney.shopping.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityYourCartBinding
import com.example.pocketmoney.shopping.adapters.CartItemListAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.ui.checkoutorder.CheckoutOrder
import com.example.pocketmoney.shopping.ui.checkoutorder.NewCheckout
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YourCart : BaseActivity<ActivityYourCartBinding>(ActivityYourCartBinding::inflate), CartItemListAdapter.CartItemListAdapterListener, ApplicationToolbar.ApplicationToolbarListener {

    //Ui

    // ViewModels
    private val viewModel: CartViewModel by viewModels()

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

        viewModel.userID.observe(this, {
            userID = it
            viewModel.getCartItems(userID)
        })
        viewModel.cartItems.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        cartIsEmpty(it)
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

        viewModel.cartItemQuantity.observe(this, { _result ->
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

    private fun cartIsEmpty(cartList: List<CartModel>) {
        if (cartList.size == 0) {
            binding.btnCheckout.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
            cartItemListAdapter.setCartItemList(cartList)
            binding.layoutCartSummary.visibility = View.GONE
        } else {
            binding.btnCheckout.visibility = View.VISIBLE
            populateValues(cartList)
            cartItemListAdapter.setCartItemList(cartList)
        }
    }

    private fun initialUiState() {
        binding.btnCheckout.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
//        binding.progressBar.visibility = View.GONE
        binding.layoutCartSummary.visibility = View.GONE
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

    private fun populateValues(cartList: List<CartModel>) {

        binding.layoutCartSummary.visibility = View.VISIBLE
        var itemQuantity: Int = 0
        var productOldPrice: Double = 0.0
        var saving: Double = 0.0
        var totalPrice: Double = 0.0

        for (item in cartList) {
            itemQuantity += item.Quantity
            productOldPrice += item.Old_Price * item.Quantity
            totalPrice += item.Price * item.Quantity
        }

        saving = productOldPrice - totalPrice


        binding.layoutCartSummary.setAmountSummary(
                ModelOrderAmountSummary(
                        itemQuantity,
                        productOldPrice,
                        saving,
                        totalPrice)
        )
        binding.layoutCartSummary.setVisibilityStatus(0)

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