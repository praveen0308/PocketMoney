package com.jmm.kyc.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jmm.kyc.R
import com.jmm.kyc.databinding.ActivityKycBinding
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KycActivity : BaseActivity<ActivityKycBinding>(ActivityKycBinding::inflate) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = Navigation.findNavController(this, R.id.nav_host_kyc)
        appBarConfiguration =
            AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()
        binding.toolbarKyc.setupWithNavController(navController,appBarConfiguration)

    }
    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun subscribeObservers() {

    }
}