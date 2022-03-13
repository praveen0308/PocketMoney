package com.jmm.checkout

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jmm.checkout.databinding.ActivityCheckout1Binding
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Checkout1 : BaseActivity<ActivityCheckout1Binding>(ActivityCheckout1Binding::inflate) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(this,R.id.nav_host_checkout1)
        appBarConfiguration =
            AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()
        binding.toolbarCheckout.setupWithNavController(navController,appBarConfiguration)

    }
    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun subscribeObservers() {

    }
}