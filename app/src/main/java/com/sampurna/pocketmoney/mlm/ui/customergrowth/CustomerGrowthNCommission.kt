package com.sampurna.pocketmoney.mlm.ui.customergrowth

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.sampurna.pocketmoney.databinding.ActivityCustomerGrowthNCommissionBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerGrowthNCommission : BaseActivity<ActivityCustomerGrowthNCommissionBinding>(ActivityCustomerGrowthNCommissionBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarCustomerGrowth.setApplicationToolbarListener(this)
        val type  = intent.getSerializableExtra("TYPE") as NavigationEnum
////        showFragment(GrowthNCommissionHome.newInstance(type))
//        val navHostFragment = nav_host_growth_n_commission as NavHostFragment
//        val graphInflater = navHostFragment.navController.navInflater
//        navGraph = graphInflater.inflate(R.navigation.nav_customer_growth_n_commission)
//        navController = navHostFragment.navController
//        navGraph.startDestination = R.id.growthNCommissionHome
//        val bundle = Bundle()
//        bundle.putSerializable("type", type)
//        navController.setGraph(navGraph,bundle)

    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        super.onBackPressed()
    }

    override fun onMenuClick() {

    }
}