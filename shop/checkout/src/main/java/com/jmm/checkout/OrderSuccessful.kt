package com.jmm.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jmm.checkout.adapters.CartItemListAdapter
import com.jmm.checkout.databinding.ActivityOrderSuccessfulBinding

class OrderSuccessful : AppCompatActivity() {

    //UI
    private lateinit var binding: ActivityOrderSuccessfulBinding

    // ViewModels
//    private val cartViewModel: CartViewModel by viewModels()

    // Adapters
    private lateinit var cartItemListAdapter: CartItemListAdapter

    // Variable
    private var selectedAddressId: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessfulBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinueShopping.setOnClickListener {
            finish()
        }
    }
}