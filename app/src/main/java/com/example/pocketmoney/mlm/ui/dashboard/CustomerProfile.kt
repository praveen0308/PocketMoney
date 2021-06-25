package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.pocketmoney.databinding.ActivityCustomerProfileBinding
import com.example.pocketmoney.mlm.model.mlmModels.CustomerProfileModel
import com.example.pocketmoney.mlm.viewmodel.CustomerViewModel
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerProfile : AppCompatActivity(), ApplicationToolbar.ApplicationToolbarListener {


    //UI
    private lateinit var binding: ActivityCustomerProfileBinding
    private lateinit var progressBarHandler: ProgressBarHandler

    // ViewModels
    private val customerViewModel by viewModels<CustomerViewModel>()
    private val userAuthenticationViewModel by viewModels<AccountViewModel>()


    // Adapters

    // Data
    private lateinit var customerProfileModel:CustomerProfileModel


    // Variable
    private var userID: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBarHandler = ProgressBarHandler(this)

        subscribeObservers()
        binding.apply {
            toolbarUserProfile.setApplicationToolbarListener(this@CustomerProfile)
        }


    }

    private fun subscribeObservers() {
        userAuthenticationViewModel.userID.observe(this, {
            userID = it
            customerViewModel.getUserProfileInfo(userID)
        })

        customerViewModel.customerProfileInfo.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        customerProfileModel = it
                        setupViewPager()
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
    }

    private fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }


    private fun displayRefreshing(loading: Boolean) {
//        binding.swipeRefreshLayout.isRefreshing = loading
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }


    fun initialUiState() {
        binding.apply {

        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    private fun setupViewPager() {
        binding.apply {

            vpUserProfile.adapter = CustomerProfileVPAdapter(this@CustomerProfile)
            val tabLayoutMediator = TabLayoutMediator(
                tlUserProfile, vpUserProfile
            ) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Profile Details"
//                    tab.setIcon(R.drawable.ic_icon_income)
//                    tab.icon!!.setTintList(null)
                    }
                    1 -> {
                        tab.text = "Address Details"

                    }
                }
            }
            tabLayoutMediator.attach()
            vpUserProfile.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            })

        }

    }
    inner class CustomerProfileVPAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CustomerProfileDetails.newInstance(customerProfileModel)
                1 -> CustomerAddressDetails.newInstance(customerProfileModel)
                else -> CustomerProfileDetails()

            }
        }

        override fun getItemCount(): Int {
            return 2
        }


    }


}
