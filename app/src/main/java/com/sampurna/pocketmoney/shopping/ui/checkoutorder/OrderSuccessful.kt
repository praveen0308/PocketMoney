package com.sampurna.pocketmoney.shopping.ui.checkoutorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.sampurna.pocketmoney.databinding.ActivityOrderSuccessfulBinding
import com.sampurna.pocketmoney.shopping.adapters.CartItemListAdapter
import com.sampurna.pocketmoney.shopping.viewmodel.CartViewModel

class OrderSuccessful : AppCompatActivity() {

    //UI
    private lateinit var binding: ActivityOrderSuccessfulBinding

    // ViewModels
    private val cartViewModel: CartViewModel by viewModels()

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