package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityPaymentHistoryFilterBinding
import com.example.pocketmoney.mlm.adapters.PaymentHistoryFilterAdapter
import com.example.pocketmoney.mlm.adapters.PaymentHistoryFilterItemAdapter
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.model.UniversalFilterModel
import com.example.pocketmoney.mlm.viewmodel.FilterViewModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentHistoryFilter : AppCompatActivity(), PaymentHistoryFilterItemAdapter.PaymentHistoryDialogListener,
    ApplicationToolbar.ApplicationToolbarListener {


    // Ui
    private lateinit var binding: ActivityPaymentHistoryFilterBinding
    private lateinit var progressBarHandler: ProgressBarHandler

    //Adapter
    private lateinit var paymentHistoryFilterAdapter: PaymentHistoryFilterAdapter

    // ViewModel
    private val filterViewModel by viewModels<FilterViewModel>()

    // Variables
    private var mFilterList= mutableListOf<UniversalFilterModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBarHandler = ProgressBarHandler(this)
        setupRecyclerview()
        binding.toolbarPaymentHistoryFilter.setApplicationToolbarListener(this)
        subscribeObservers()

        filterViewModel.getFilterList()

        binding.layoutFilterAction.btnApplyFilter.setOnClickListener {
            filterViewModel.updateFilterList(mFilterList)
            finish()
        }
    }

    private fun subscribeObservers() {
        filterViewModel.paymentHistoryFilterList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

                        mFilterList = it.toMutableList()
                        paymentHistoryFilterAdapter.setFilterList(mFilterList)
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


    private fun setupRecyclerview() {
        paymentHistoryFilterAdapter = PaymentHistoryFilterAdapter(this)
        binding.apply {
            rvFilterList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = paymentHistoryFilterAdapter
            }
        }

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

    override fun onSingleItemClick(index: Int, itemList: MutableList<UniversalFilterItemModel>) {
        mFilterList[index-1].filterList = itemList
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}