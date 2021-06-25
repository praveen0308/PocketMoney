package com.example.pocketmoney.shopping.ui.customernavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyOrders : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
    }
}