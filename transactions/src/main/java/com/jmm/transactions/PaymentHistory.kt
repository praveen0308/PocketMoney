package com.jmm.transactions

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jmm.core.utils.getDateRange
import com.jmm.core.utils.getTodayDate
import com.jmm.model.TransactionModel
import com.jmm.model.mlmModels.CustomerRequestModel1
import com.jmm.model.myEnums.DateTimeEnum
import com.jmm.model.myEnums.PaymentHistoryFilterEnum
import com.jmm.transactions.databinding.FragmentPaymentHistoryBinding
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentHistory : BaseFragment<FragmentPaymentHistoryBinding>(FragmentPaymentHistoryBinding::inflate), SwipeRefreshLayout.OnRefreshListener,
    PaymentHistoryAdapter.PaymentHistoryInterface {

    // Ui

    //Adapter
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter

    // ViewModel

    private val viewModel by viewModels<PaymentHistoryViewModel>()

    // Variables
    private var userID: String = ""
    private var roleID: Int = 0
    private var paymentsList = mutableListOf<TransactionModel>()
    private var activeCategoryFilter=PaymentHistoryFilterEnum.ALL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.imgFilter.setOnClickListener {
            /*val intent = Intent(requireActivity(), PaymentHistoryFilter::class.java)
            startActivity(intent)*/
        }


        binding.refreshLayout.setOnRefreshListener(this)
    }


    private fun requestTransactionHistory(userID: String, roleID: Int, fromDate: String, toDate: String) {
        if (userID.isEmpty()) checkAuthorization()
        else{
            val requestModel = CustomerRequestModel1(
                FromDate = fromDate,
                ToDate = toDate,
                RoleID = roleID,
                UserID = userID.toLong()
            )

            viewModel.getAllTransactionHistory(requestModel)
        }

    }

    override fun subscribeObservers() {
        viewModel.allTransactionHistory.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        paymentsList = it.toMutableList()

                        when (activeCategoryFilter) {
                            PaymentHistoryFilterEnum.CREDIT -> {
                                paymentsList.filter { listItem ->
                                    listItem.Debit == 0.0
                                }
                            }

                            PaymentHistoryFilterEnum.DEBIT -> {
                                paymentsList.filter { listItem ->
                                    listItem.Credit == 0.0
                                }
                            }
                            else -> {
                                paymentsList
                            }
                        }
                        paymentHistoryAdapter.setTransactionHistoryList(paymentsList)
                     /*   if (transactionTypeFilter.contains("All Transactions")) {
                            paymentHistoryAdapter.setTransactionHistoryList(filteredByCategoryList)
                        } else {
                            val filteredByTypeList =
                                filteredByCategoryList.filter { transactionModel ->
                                    transactionModel.Trans_Category in transactionTypeFilter
                                }
                            paymentHistoryAdapter.setTransactionHistoryList(filteredByTypeList)
                        }
*/
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
        }


        viewModel.userId.observe(viewLifecycleOwner) {
            userID = it
            if (userID.isEmpty()) {
                checkAuthorization()
            }

        }
        viewModel.userRoleID.observe(viewLifecycleOwner) {
            roleID = it
            if (userID != "" && roleID != 0) {
                viewModel.getWalletBalance(userID, roleID)
                requestTransactionHistory(
                    userID,
                    roleID,
                    getDateRange(DateTimeEnum.LAST_MONTH),
                    getTodayDate()
                )
            } else {
                checkAuthorization()
            }

        }
        viewModel.walletBalance.observe(viewLifecycleOwner) { _result ->
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
        }


    }



    private fun setupRecyclerView() {
        paymentHistoryAdapter = PaymentHistoryAdapter(this)
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

    override fun onPaymentHistoryClick(transactionModel: TransactionModel) {
        val intent = Intent(requireActivity(),TransactionDetailActivity::class.java)
        intent.putExtra("TransactionID",transactionModel.Trans_Id)
        startActivity(intent)
    }
}