package com.example.pocketmoney.mlm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityMainDashboardBinding
import com.example.pocketmoney.mlm.ui.customergrowth.CustomerGrowthNCommission
import com.example.pocketmoney.mlm.ui.dashboard.pages.Home
import com.example.pocketmoney.mlm.ui.dashboard.pages.Shop
import com.example.pocketmoney.shopping.ui.*
import com.example.pocketmoney.shopping.ui.customernavigation.*
import com.example.pocketmoney.utils.MyCustomNavigationDrawer.ModelItem
import com.example.pocketmoney.utils.MyCustomNavigationDrawer.ModelSubItem
import com.example.pocketmoney.utils.MyCustomNavigationDrawer.MyNavigationDrawer
import com.example.pocketmoney.utils.myEnums.NavigationEnum
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainDashboard : AppCompatActivity(), Home.HomeFragmentListener, Shop.ShopFragmentListener, NavigationView.OnNavigationItemSelectedListener,
    MyNavigationDrawer.MyNavigationDrawerInterface {

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

        //Setting the navigation controller to Bottom Nav

//        binding.mainDashboardUserNavigationDrawer.bringToFront()

        binding.mainDashboardShoppingNavigationDrawer.bringToFront()


        binding.mainDashboardDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, binding.mainDashboardShoppingNavigationDrawer)
//        binding.customUserNavigation.setNavigationItemList(getUserNavigationItems())
//        binding.customUserNavigation.setNavigationListener(this)

    }

    private fun getUserNavigationItems() : List<ModelItem>{
        val navigationList = mutableListOf<ModelItem>()

        val incomeSubItemList = mutableListOf<ModelSubItem>()
        incomeSubItemList.add(ModelSubItem("Growth",NavigationEnum.GROWTH))
        incomeSubItemList.add(ModelSubItem("Commission",NavigationEnum.COMMISSION))
        navigationList.add(ModelItem("Income",incomeSubItemList,NavigationEnum.PARENT_MENU))

        val walletSubItemList = mutableListOf<ModelSubItem>()
        walletSubItemList.add(ModelSubItem("Wallet",NavigationEnum.WALLET))
        walletSubItemList.add(ModelSubItem("P Cash",NavigationEnum.P_CASH))
        walletSubItemList.add(ModelSubItem("My Coupon",NavigationEnum.MY_COUPON))
        walletSubItemList.add(ModelSubItem("Create Coupon",NavigationEnum.CREATE_COUPON))
        navigationList.add(ModelItem("Wallet",walletSubItemList,NavigationEnum.PARENT_MENU))

        navigationList.add(ModelItem("Profile", mutableListOf(),NavigationEnum.PROFILE))

        navigationList.add(ModelItem("Downline", mutableListOf(),NavigationEnum.DOWNLINE))

        navigationList.add(ModelItem("Offer", mutableListOf(),NavigationEnum.OFFER))

        val reportSubItemList = mutableListOf<ModelSubItem>()
        reportSubItemList.add(ModelSubItem("Complaint History",NavigationEnum.TICKET_HISTORY))
        reportSubItemList.add(ModelSubItem("Transaction History",NavigationEnum.ALL_TRANSACTION))
        navigationList.add(ModelItem("Report",reportSubItemList,NavigationEnum.PARENT_MENU))
        navigationList.add(ModelItem("Logout", mutableListOf(),NavigationEnum.LOG_OUT))


        return navigationList
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
//            R.id.choose_language -> {
//                Toast.makeText(this, "Coming soon!!", Toast.LENGTH_SHORT).show()
//            //                val intent = Intent(this, YourCart::class.java)
////                startActivity(intent)
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

            R.id.my_notifications -> {
                val intent = Intent(this, MyNotifications::class.java)
                startActivity(intent)

            }

//            R.id.my_account -> {
//                val intent = Intent(this, MyAccount::class.java)
//                startActivity(intent)
//
//            }
//            R.id.notification_preferences -> {
//                val intent = Intent(this, ExtraNavigationActivity::class.java)
//                intent.putExtra("SOURCE",1)
//                startActivity(intent)
//
//            }
//            R.id.help_centre -> {
//                val intent = Intent(this, ExtraNavigationActivity::class.java)
//                intent.putExtra("SOURCE",2)
//                startActivity(intent)
//
//            }
//            R.id.privacy_policy -> {
//                val intent = Intent(this, ExtraNavigationActivity::class.java)
//                intent.putExtra("SOURCE",3)
//                startActivity(intent)
//
//            }
            else->startActivity(Intent(this,YourCart::class.java))

        }
        binding.mainDashboardDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNavigationItemClick(action: NavigationEnum) {
        when(action){
            NavigationEnum.COMMISSION->
            {
//                val intent = Intent(this,CustomerCommissionActivity::class.java)
                val intent = Intent(this, CustomerGrowthNCommission::class.java)
                intent.putExtra("TYPE",NavigationEnum.COMMISSION)
                startActivity(intent)
            }
            NavigationEnum.GROWTH-> {
//                val intent = Intent(this,CustomerGrowthActivity::class.java)
                val intent = Intent(this, CustomerGrowthNCommission::class.java)
                intent.putExtra("TYPE",NavigationEnum.GROWTH)
                startActivity(intent)
            }

            NavigationEnum.PROFILE-> {
                val intent = Intent(this,CustomerProfile::class.java)
                startActivity(intent)
            }

            NavigationEnum.MY_COUPON->startActivity(Intent(this,MyCoupons::class.java))
            NavigationEnum.TICKET_HISTORY->startActivity(Intent(this,ComplaintList::class.java))
            NavigationEnum.ALL_TRANSACTION->{
                navController.navigate(R.id.paymentHistory)
                binding.root.closeDrawer(GravityCompat.END)

            }

            NavigationEnum.WALLET-> {
                val intent = Intent(this,CustomerWalletActivity::class.java)
                intent.putExtra("FILTER","BUSINESS")
                startActivity(intent)
            }
            NavigationEnum.P_CASH-> {
                val intent = Intent(this,CustomerWalletActivity::class.java)
                intent.putExtra("FILTER","INCOME")
                startActivity(intent)
            }
        }
    }


}