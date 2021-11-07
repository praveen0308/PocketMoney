package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentPayoutTransactionsBinding
import com.sampurna.pocketmoney.mlm.adapters.PayoutTransactionsAdapter
import com.sampurna.pocketmoney.mlm.model.payoutmodels.PayoutTransaction
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
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