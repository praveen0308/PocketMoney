package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.pocketmoney.databinding.ActivityCustomerProfileBinding
import com.example.pocketmoney.mlm.model.mlmModels.CustomerProfileModel
import com.example.pocketmoney.mlm.viewmodel.CustomerProfileViewModel
import com.example.pocketmoney.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerProfile : BaseActivity<ActivityCustomerProfileBinding>(ActivityCustomerProfileBinding::inflate), ApplicationToolbar.ApplicationToolbarListener {


    // ViewModels
    private val viewModel by viewModels<CustomerProfileViewModel>()

    // Adapters

    // Data
    private lateinit var customerProfileModel:CustomerProfileModel


    // Variable
    private var userID: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            toolbarUserProfile.setApplicationToolbarListener(this@CustomerProfile)
        }


    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userID = it
            viewModel.getUserProfileInfo(userID)
        })

        viewModel.customerProfileInfo.observe(this, { _result ->
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
