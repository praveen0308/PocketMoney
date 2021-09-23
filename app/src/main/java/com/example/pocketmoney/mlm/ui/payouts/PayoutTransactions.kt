package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPayoutTransactionsBinding
import com.example.pocketmoney.mlm.adapters.BeneficiaryListAdapter
import com.example.pocketmoney.mlm.adapters.PayoutTransactionsAdapter
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutTransaction
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayoutTransactions : BaseFragment<FragmentPayoutTransactionsBinding>(FragmentPayoutTransactionsBinding::inflate),
    PayoutTransactionsAdapter.PayoutTransactionsInterface {

    private val viewModel by activityViewModels<PayoutViewModel>()

    private var payoutType = 1
    private var customerNumber = ""
    private lateinit var payoutTransactionsAdapter: PayoutTransactionsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvData()
    }

    override fun subscribeObservers() {
        viewModel.payoutType.observe(viewLifecycleOwner,{
            payoutType = it
            if (!customerNumber.isNullOrEmpty()){
                viewModel.getPayoutTransactions(customerNumber,payoutType)
            }

        })

        viewModel.customerNumber.observe(viewLifecycleOwner,{
            customerNumber = it
            viewModel.getPayoutTransactions(customerNumber,payoutType)
        })
        viewModel.payoutTransactions.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        payoutTransactionsAdapter.setPayoutTransactionList(it)
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
    private fun setupRvData(){
        payoutTransactionsAdapter = PayoutTransactionsAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter = payoutTransactionsAdapter
        }

    }

    override fun onPayoutTransactionClick(transaction: PayoutTransaction) {

    }
}