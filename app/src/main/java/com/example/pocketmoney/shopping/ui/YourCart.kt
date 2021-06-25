package com.example.pocketmoney.shopping.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityYourCartBinding
import com.example.pocketmoney.shopping.adapters.CartItemListAdapter
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.ui.checkoutorder.CheckoutOrder
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.ModelOrderAmountSummary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YourCart : AppCompatActivity(), CartItemListAdapter.CartItemListAdapterListener, ApplicationToolbar.ApplicationToolbarListener {

    //Ui
    private lateinit var binding: ActivityYourCartBinding

    // ViewModels
    private val cartViewModel: CartViewModel by viewModels()

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter

    // Variable
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYourCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialUiState()
        binding.toolbarYourCart.setApplicationToolbarListener(this)
        setUpCartItemRecyclerView()
        subscribeObservers()
        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutOrder::class.java))
        }

    }

    private fun subscribeObservers() {

        cartViewModel.userID.observe(this, {
            userID = it
            cartViewModel.getCartItems(userID)
        })

        cartViewModel.cartItems.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success<List<CartModel>> -> {
                    displayLoading(false)
                    cartIsEmpty(dataState.data)
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

        cartViewModel.cartItemQuantity.observe(this, { dataState ->
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
        binding.progressBar.visibility = View.GONE
        binding.layoutCartSummary.visibility = View.GONE
    }

    private fun displayLoading(state: Boolean) {

        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show()
        }
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
        cartViewModel.changeCartItemQuantity(1, itemID, userID)
    }

    override fun onItemQuantityDecrease(itemID: Int) {
        cartViewModel.changeCartItemQuantity(0, itemID, userID)
    }

    override fun onItemDelete(itemID: Int) {
        cartViewModel.changeCartItemQuantity(-1, itemID, userID)
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