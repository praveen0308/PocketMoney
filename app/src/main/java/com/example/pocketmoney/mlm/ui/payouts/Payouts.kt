package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityPayoutsBinding
import com.example.pocketmoney.mlm.ui.dashboard.CustomerProfileDetails
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Payouts : BaseActivity<ActivityPayoutsBinding>(ActivityPayoutsBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<PayoutViewModel>()
    private var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivityPayout.setApplicationToolbarListener(this)
        setupViewPager()
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{

        })
    }


    private fun setupViewPager() {
        binding.apply {

            vpPayouts.adapter = PayoutsVPAdapter(this@Payouts)
            val tabLayoutMediator = TabLayoutMediator(
                tlPayouts, vpPayouts
            ) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Bank Transfer"
                        tab.icon = ContextCompat.getDrawable(this@Payouts,R.drawable.ic_bank)
//                    tab.setIcon(R.drawable.ic_icon_income)
//                    tab.icon!!.setTintList(null)
                    }
                    1 -> {
                        tab.text = "Paytm Transfer"
                        tab.icon = ContextCompat.getDrawable(this@Payouts,R.drawable.ic_paytm_logo)
                    }
                }
            }
            tabLayoutMediator.attach()
            vpPayouts.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            })

        }

    }
    inner class PayoutsVPAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BankTransfer()
                1 -> WalletToPaytmTransfer()
                else -> CustomerProfileDetails()

            }
        }

        override fun getItemCount(): Int {
            return 2
        }


    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {
    }

}