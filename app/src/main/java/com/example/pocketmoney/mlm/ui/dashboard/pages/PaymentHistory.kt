package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pocketmoney.databinding.FragmentPaymentHistoryBinding
import com.example.pocketmoney.mlm.adapters.PaymentHistoryAdapter
import com.example.pocketmoney.mlm.model.TransactionModel
import com.example.pocketmoney.mlm.model.TransactionTypeModel
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.viewmodel.FilterViewModel
import com.example.pocketmoney.mlm.viewmodel.PaymentHistoryViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.example.pocketmoney.utils.myEnums.PaymentHistoryFilterEnum
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentHistory : BaseFragment<FragmentPaymentHistoryBinding>(FragmentPaymentHistoryBinding::inflate), SwipeRefreshLayout.OnRefreshListener {

    // Ui

    //Adapter
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter

    // ViewModel
    private val filterViewModel by viewModels<FilterViewModel>()
    private val viewModel by viewModels<PaymentHistoryViewModel>()

    // Variables
    private var userID: String = ""
    private var roleID: Int = 0
    private var paymentsList = mutableListOf<TransactionModel>()
    private var activeTimeFilter=PaymentHistoryFilterEnum.LAST_MONTH
    private var activeCategoryFilter=PaymentHistoryFilterEnum.ALL
    private var transactionTypeFilter= listOf("All Transactions")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.imgFilter.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentHistoryFilter::class.java)
            startActivity(intent)
        }

        filterViewModel.getFilterList()
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        filterViewModel.paymentHistoryFilterList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isEmpty()) {
                            filterViewModel.getTransactionType()
                        } else {

                            activeTimeFilter = it[0].filterList.filter { item -> item.isSelected }[0].ID as PaymentHistoryFilterEnum
                            activeCategoryFilter = it[1].filterList.filter { item -> item.isSelected }[0].ID as PaymentHistoryFilterEnum
                            transactionTypeFilter = it[2].filterList.filter { item->item.isSelected }.map {
                                model -> model.displayText
                            }
//                            sb.append(it[2].filterList.filter { item->item.isSelected })
                            if (activeTimeFilter == PaymentHistoryFilterEnum.CUSTOM) {
                                val materialDateBuilder =
                                        MaterialDatePicker.Builder.dateRangePicker()
                                materialDateBuilder.setTitleText("SELECT A DATE")

                                val materialDatePicker = materialDateBuilder.build()
                                materialDatePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")

                                materialDatePicker.addOnPositiveButtonClickListener { selection ->
                                    val startDate = convertMillisecondsToDate(selection.first, "yyyy-MM-dd")
                                    val endDate = convertMillisecondsToDate(selection.second, "yyyy-MM-dd")

                                    requestTransactionHistory(userID, roleID, startDate, endDate)

                                    //Do something...
                                }
                            } else {
                                requestTransactionHistory(userID, roleID, getDateRange(activeTimeFilter), getTodayDate())
                            }





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

    private fun requestTransactionHistory(userID: String, roleID: Int, fromDate: String, toDate: String) {
        val requestModel = CustomerRequestModel1(
                FromDate = fromDate,
                ToDate = toDate,
                RoleID = roleID,
                UserID = userID.toLong()
        )

        viewModel.getAllTransactionHistory(requestModel)
    }

    override fun subscribeObservers() {
        viewModel.allTransactionHistory.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        paymentsList = it.toMutableList()

                        val filteredByCategoryList = when (activeCategoryFilter) {
                            PaymentHistoryFilterEnum.CREDIT -> {
                                paymentsList.filter { listItem ->
                                    listItem.Debit == 0.0
                                }
                            }

                            PaymentHistoryFilterEnum.DEBIT -> {
                                paymentsList.filter { listItem ->
                                    listItem.Credit ==  0.0
                                }
                            }
                            else->{
                                paymentsList
                            }
                        }

                        if (transactionTypeFilter.contains("All Transactions")){
                            paymentHistoryAdapter.setTransactionHistoryList(filteredByCategoryList)
                        }else{
                            val filteredByTypeList = filteredByCategoryList.filter {transactionModel->
                                transactionModel.Trans_Category in transactionTypeFilter
                            }
                            paymentHistoryAdapter.setTransactionHistoryList(filteredByTypeList)
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


        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it

        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getWalletBalance(userID, roleID)
                requestTransactionHistory(userID, roleID, getDateRange(DateTimeEnum.LAST_MONTH), getTodayDate())
            }

        })
        viewModel.walletBalance.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.layoutPaymentHistory.tvBalanceAmount.text = "₹".plus(it.toString())
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
        filterViewModel.transactionType.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        filterViewModel.populateFilterList(convertTransactionTypeToUniversalFilterItem(it))
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


    private fun convertTransactionTypeToUniversalFilterItem(mList: List<TransactionTypeModel>): MutableList<UniversalFilterItemModel> {
        val filterItemList = mutableListOf<UniversalFilterItemModel>()
        for (transactionType in mList) {
            if (transactionType.Comm_Cat_ID == 0) {
                filterItemList.add(UniversalFilterItemModel(3, PaymentHistoryFilterEnum.MULTI, transactionType.Comm_Cat_ID, transactionType.Comm_Category_Name, true))
            } else {
                filterItemList.add(UniversalFilterItemModel(3, PaymentHistoryFilterEnum.MULTI, transactionType.Comm_Cat_ID, transactionType.Comm_Category_Name))
            }
        }



        return filterItemList.reversed().toMutableList()
    }

    private fun setupRecyclerView() {
        paymentHistoryAdapter = PaymentHistoryAdapter()
        binding.rvTransactionHistory.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                    layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter = paymentHistoryAdapter
        }
    }

    override fun onRefresh() {

        binding.refreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarHandler.hide()
    }
}