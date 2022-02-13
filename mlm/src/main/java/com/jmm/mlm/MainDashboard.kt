package com.jmm.mlm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.jmm.mlm.databinding.ActivityMainDashboardBinding
import com.jmm.mlm.ui.Home
import com.jmm.mlm.ui.Shop
import com.jmm.mlm.ui.ShopDirections
import com.jmm.shopping.ui.YourCart
import com.jmm.shopping.ui.customernavigation.AllCategories
import com.jmm.shopping.ui.customernavigation.MyOrders
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainDashboard : AppCompatActivity(), Home.HomeFragmentListener, Shop.ShopFragmentListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainDashboardBinding
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Getting the Navigation Controller
        navController = Navigation.findNavController(this, R.id.nav_host_main_dashboard)

        binding.mainDashboardShoppingNavigationDrawer.setNavigationItemSelectedListener(this)
        NavigationUI.setupWithNavController(binding.bottomNavMainDashboard, navController)

        binding.mainDashboardShoppingNavigationDrawer.bringToFront()


        binding.mainDashboardDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, binding.mainDashboardShoppingNavigationDrawer)


    }

    override fun onBackPressed() {

        when {
            binding.root.isDrawerOpen(GravityCompat.END) -> {
                binding.root.closeDrawer(GravityCompat.END)
            }
            binding.root.isDrawerOpen(GravityCompat.START) -> {
                binding.root.closeDrawer(GravityCompat.START)
            }
            doubleBackToExitPressedOnce -> {
                super.onBackPressed()
            }
            else -> {
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }

    override fun onUserProfileClick() {
        binding.root.openDrawer(GravityCompat.END)
    }

    override fun onNavigationClick() {
        binding.root.openDrawer(GravityCompat.START)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.all_categories -> {
                startActivity(Intent(this, AllCategories::class.java))
            }
//            R.id.offer_zone -> {
//                val intent = Intent(this, ShoppingOfferZone::class.java)
//                startActivity(intent)
//
//            }

            R.id.my_orders -> {
                val intent = Intent(this, MyOrders::class.java)
                startActivity(intent)

            }
//            R.id.my_coupons -> {
//                val intent = Intent(this, YourCart::class.java)
//                startActivity(intent)
//
//            }
            R.id.my_cart -> {
                val intent = Intent(this, YourCart::class.java)
                startActivity(intent)

            }
//            R.id.my_wishlist -> {
//                val intent = Intent(this, MyWishlist::class.java)
//                startActivity(intent)
//            }

           /* R.id.my_notifications -> {
                *//*val intent = Intent(this, MyNotifications::class.java)
                startActivity(intent)*//*

            }*/



            R.id.my_addresses ->{
                toggleBottomNav(false)
                navController.navigate(ShopDirections.actionShopToManageAddress())
            }
            R.id.privacy_policy -> {
                toggleBottomNav(false)
                navController.navigate(ShopDirections.actionShopToPrivacyPolicy2())
//                val intent = Intent(this, ExtraNavigationActivity::class.java)
//                intent.putExtra("SOURCE",3)
//                startActivity(intent)
//                navController.navigate(R.id.action_shop_to_privacyPolicy2)

            }
            else->startActivity(Intent(this,YourCart::class.java))

        }
        binding.mainDashboardDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    fun toggleBottomNav(visibility:Boolean){
        binding.bottomNavMainDashboard.isVisible = visibility
    }




}