package com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityNewRechargeBinding
import com.example.pocketmoney.mlm.ui.dashboard.CustomerProfileDetails
import com.example.pocketmoney.mlm.ui.payouts.BankTransfer
import com.example.pocketmoney.mlm.ui.payouts.WalletToPaytmTransfer
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewRechargeActivity : BaseActivity<ActivityNewRechargeBinding>(ActivityNewRechargeBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivityRecharge.setApplicationToolbarListener(this)

    }

    override fun subscribeObservers() {

    }


    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}