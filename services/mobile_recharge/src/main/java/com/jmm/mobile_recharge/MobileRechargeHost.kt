package com.jmm.mobile_recharge

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jmm.mobile_recharge.databinding.FragmentMobileRechargeHostBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileRechargeHost : BaseFragment<FragmentMobileRechargeHostBinding>(FragmentMobileRechargeHostBinding::inflate),
    Recharge.MobileRechargeInterface {

    private val viewModel by activityViewModels<MobileRechargeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

        binding.vpRecharge.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.currentActivePage.postValue(position.toInt())
            }
        })
    }

    override fun subscribeObservers() {
        viewModel.currentActivePage.observe(viewLifecycleOwner,{
            binding.vpRecharge.currentItem = it
        })

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

                else -> RechargeHistory()

            }
        }

        override fun getItemCount(): Int {
            return 2
        }


    }

    override fun operatorSelection() {
//        findNavController().navigate(R.id.action_mobileRechargeHost_to_selectOperator)
    }
}