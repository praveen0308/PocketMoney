package com.example.pocketmoney.shopping.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.pocketmoney.R
import com.example.pocketmoney.utils.myEnums.NavigationType
import com.example.pocketmoney.utils.myEnums.ShoppingEnum

class ExtraNavigationActivity : AppCompatActivity() {

    // Navigation

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_navigation)
        val source = intent.getIntExtra("SOURCE",0)

        setupStartDestination(source)
    }
    fun setupStartDestination(step: Int) {

//        navHostFragment = findViewById(R.id.nav_host_extra_navigation) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_extra_navigation)
        navController = navHostFragment.navController

        when (NavigationType.getSource(step)) {
            NavigationType.NotificationPreferences -> {
                navGraph.startDestination = R.id.notificationPreferences

            }
            NavigationType.HelpCentre -> {
                navGraph.startDestination = R.id.helpCentre
//                val bundle = Bundle()
//                bundle.putString("OPERATOR_TYPE", "DTH")
//                navController.setGraph(navGraph,bundle)
            }
            NavigationType.PrivacyPolicy -> {
                navGraph.startDestination = R.id.privacyPolicy
            }
        }


        navController.graph = navGraph
    }
}