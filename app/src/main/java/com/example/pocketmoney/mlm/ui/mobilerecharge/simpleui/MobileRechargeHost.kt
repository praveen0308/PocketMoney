package com.example.pocketmoney.mlm.ui.mobilerecharge.simpleui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentMobileRechargeHostBinding
import com.example.pocketmoney.mlm.ui.dashboard.CustomerProfileDetails
import com.example.pocketmoney.utils.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileRechargeHost : BaseFragment<FragmentMobileRechargeHostBinding>(FragmentMobileRechargeHostBinding::inflate),
    Recharge.MobileRechargeInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun subscribeObservers() {

    }


    private fun setupViewPager() {
        binding.apply {

            vpRecharge.adapter = VPAdapter(requireActivity())
            val tabLayoutMediator = TabLayoutMediator(
                tlRecharge, vpRecharge
            ) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Recharge"
//                        tab.icon = ContextCompat.getDrawable(this@Payouts, R.drawable.ic_bank)
//                    tab.setIcon(R.drawable.ic_icon_income)
//                    tab.icon!!.setTintList(null)
                    }
                    1 -> {
                        tab.text = "History"
//                        tab.icon = ContextCompat.getDrawable(this@Payouts, R.drawable.ic_paytm_logo)
                    }


                }
            }
            tabLayoutMediator.attach()
            vpRecharge.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            })

        }

    }

    inner class VPAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Recharge(this@MobileRechargeHost)
                1 -> RechargeHistory()

                else -> CustomerProfileDetails()

            }
        }

        override fun getItemCount(): Int {
            return 2
        }


    }

    override fun operatorSelection() {
        findNavController().navigate(R.id.action_mobileRechargeHost_to_selectOperator)
    }
}