package com.example.pocketmoney.mlm.ui.payouts

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmoney.databinding.ActivityNewPayoutBinding
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutCustomer
import com.example.pocketmoney.mlm.ui.dashboard.CustomerProfileDetails
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.shopping.model.ModelCity
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.Status
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPayout : BaseActivity<ActivityNewPayoutBinding>(ActivityNewPayoutBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {
    private val viewModel by viewModels<PayoutViewModel>()
    var btnState = "SEARCH"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarActivityPayout.setApplicationToolbarListener(this)
        setupViewPager()
        populatePayoutTypes()
        binding.etSearchView.doAfterTextChanged {
            if (it.toString().length < 10){
                viewModel.btnState.postValue(0)
            }else{
                viewModel.btnState.postValue(1)
            }
        }

        binding.btnSearch.setOnClickListener {
            viewModel.customerNumber.postValue(binding.etSearchView.text.toString().trim())
            if (btnState == "ADD"){
                val sheet = AddPayoutCustomer()
                sheet.show(supportFragmentManager,sheet.tag)
            }
            else{
                viewModel.searchPayoutCustomer(binding.etSearchView.text.toString())
            }
        }

    }

    override fun subscribeObservers() {
        viewModel.payoutType.observe(this,{

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
                    if (_result._data ==null){
                        viewModel.btnState.postValue(2)
                    }else{
                        populateCustomerUI(_result._data)
                        binding.apply {
                            tlPayouts.isVisible = true
                            vpPayouts.isVisible= true
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


    private fun populatePayoutTypes(){
        val types = arrayListOf("Bank transfer","Paytm transfer","UPI transfer")
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, types)
        binding.actvPayoutType.apply {
            threshold = 1
            setAdapter(arrayAdapter)

            setOnItemClickListener { parent, _, position, _ ->
                val type = parent.getItemAtPosition(position) as String
                when(type){
                    "Bank transfer"->viewModel.payoutType.postValue(1)
                    "UPI transfer"->viewModel.payoutType.postValue(2)
                    "Paytm transfer"->viewModel.payoutType.postValue(3)


                }

            }
        }

    }

    private fun setupViewPager() {
        binding.apply {

            vpPayouts.adapter = PayoutsVPAdapter(this@NewPayout)
            val tabLayoutMediator = TabLayoutMediator(
                tlPayouts, vpPayouts
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = "Beneficiary's List"
                    1 -> tab.text = "Transaction Details"
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
                0 -> BeneficiaryList()
                1 -> PayoutTransactions()
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