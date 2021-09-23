package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityPayoutsBinding
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutCustomer
import com.example.pocketmoney.mlm.ui.dashboard.CustomerProfileDetails
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.Status
import com.google.android.material.tabs.TabLayoutMediator
import com.paytm.pgsdk.PaytmOrder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Payouts : BaseActivity<ActivityPayoutsBinding>(ActivityPayoutsBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<PayoutViewModel>()
    private var userId = ""

    var btnState = "SEARCH"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivityPayout.setApplicationToolbarListener(this)
        setupViewPager()

        binding.etSearchView.doAfterTextChanged {
            if (it.toString().length < 10){
                viewModel.btnState.postValue(0)
            }else{
                viewModel.btnState.postValue(1)
            }
        }

        binding.btnSearch.setOnClickListener {
            viewModel.customerNumber.postValue(binding.etSearchView.text.toString().trim())
            if (btnState.equals("ADD")){
                val sheet = AddPayoutCustomer()
                sheet.show(supportFragmentManager,sheet.tag)
            }
            else{
                viewModel.searchPayoutCustomer(binding.etSearchView.text.toString())
            }
        }


    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this, {

        })

        viewModel.btnState.observe(this, {
            when (it) {
                0 -> {
                    btnState = "SEARCH"
                    binding.btnSearch.text = "Search"
                    binding.btnSearch.isEnabled = false

                }
                1 -> {
                    btnState = "SEARCH"
                    binding.btnSearch.text = "Search"
                    binding.btnSearch.isEnabled = true
                }
                2 -> {
                    btnState = "ADD"
                    binding.btnSearch.text = "Add Customer"
                    binding.btnSearch.isEnabled = true
                }
            }
        })

        viewModel.payoutCustomer.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        if (it==null){
                            viewModel.btnState.postValue(2)
                        }else{
                            populateCustomerUI(it)
                        }
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

    private fun populateCustomerUI(customer: PayoutCustomer) {
        binding.apply {
            layoutCustomerDetail.root.isVisible =true
            layoutCustomerDetail.apply {
                tvSymbol.text = customer.FirstName?.take(1)
                tvName.text = "${customer.FirstName} ${customer.LastName}"
                tvSenderId.text = customer.PayoutCustomerID
                tvLimit.text = customer.TransferLimit.toString()
            }
        }
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
                        tab.icon = ContextCompat.getDrawable(this@Payouts, R.drawable.ic_bank)
//                    tab.setIcon(R.drawable.ic_icon_income)
//                    tab.icon!!.setTintList(null)
                    }
                    1 -> {
                        tab.text = "Paytm Transfer"
                        tab.icon = ContextCompat.getDrawable(this@Payouts, R.drawable.ic_paytm_logo)
                    }
                    2 -> {
                        tab.text = "UPI Pay"
                        tab.icon = ContextCompat.getDrawable(this@Payouts, R.drawable.img_upi)
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
                2 -> WalletToPaytmTransfer()
                else -> CustomerProfileDetails()

            }
        }

        override fun getItemCount(): Int {
            return 3
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

}