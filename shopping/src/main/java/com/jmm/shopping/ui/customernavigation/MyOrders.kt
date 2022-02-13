package com.jmm.shopping.ui.customernavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jmm.shopping.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyOrders : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
    }
}