package com.jmm.mlm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.jmm.core.adapters.CustomerTransactionHistoryAdapter
import com.jmm.core.utils.convertMillisecondsToDate
import com.jmm.core.utils.getDateRange
import com.jmm.core.utils.getTimeFilter
import com.jmm.core.utils.getTodayDate
import com.jmm.mlm.databinding.ActivityCustomerWalletBinding
import com.jmm.mlm.viewmodel.CustomerWalletViewModel
import com.jmm.model.TransactionModel
import com.jmm.model.UniversalFilterItemModel
import com.jmm.model.mlmModels.CustomerRequestModel1
import com.jmm.model.myEnums.DateTimeEnum
import com.jmm.model.shopping_models.ModelProductVariant
import com.jmm.navigation.NavRoute.B2BTransfer
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerWalletActivity : BaseActivity<ActivityCustomerWalletBinding>(ActivityCustomerWalletBinding::inflate), ApplicationToolbar.ApplicationToolbarListener, CustomerTransactionHistoryAdapter.CustomerTransactionHistoryAdapterInterface {

    //UI
    // ViewModels
    private val viewModel by viewModels<CustomerWalletViewModel>()

    // Adapters
    private lateinit var customerTransactionHistoryAdapter: CustomerTransactionHistoryAdapter

    // List
    private lateinit var productVariantList: MutableList<ModelProductVariant>

    // Variable
    private var filter: String = ""
    private var userID: String = ""
    private var roleID: Int = 0
    private var selectedCommission: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filter = intent.getStringExtra("FILTER").toString()
        if(filter=="INCOME") binding.fabTransfer.isVisible =false
        populateTimeFilter(getTimeFilter().toMutableList())
        setupRecyclerview()
        binding.toolbarCustomerWallet.setApplicationToolbarListener(this)
        binding.fabTransfer.setOnClickListener {
            startActivity(Intent(this, Class.forName(B2BTransfer)))
        }
    }

    private fun populateTimeFilter(stateList: MutableList<UniversalFilterItemModel>) {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, stateList)
        //actv is the AutoCompleteTextView from your layout file
        binding.actvTimeFilter.threshold = 1 //start searching for values after typing first character
        binding.actvTimeFilter.setAdapter(arrayAdapter)
        initialUiState()
        binding.actvTimeFilter.setOnItemClickListener { parent, view, position, id ->

            val filterItem = parent.getItemAtPosition(position) as UniversalFilterItemModel

            if (filterItem.ID as DateTimeEnum == DateTimeEnum.CUSTOM) {
                val materialDateBuilder =
                        MaterialDatePicker.Builder.dateRangePicker()
                materialDateBuilder.setTitleText("SELECT A DATE")

                val materialDatePicker = materialDateBuilder.build()
                materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")

                materialDatePicker.addOnPositiveButtonClickListener { selection ->
                    val startDate = convertMillisecondsToDate(selection.first, "yyyy-MM-dd")
                    val endDate = convertMillisecondsToDate(selection.second, "yyyy-MM-dd")

                    val requestModel = CustomerRequestModel1(
                            UserID = userID.toLong(), RoleID = roleID, FromDate = startDate, ToDate = endDate,Filter = filter
                    )
                    viewModel.getTransactionHistory(requestModel)

                    //Do something...
                }

            } else {

                val requestModel = CustomerRequestModel1(
                        UserID = userID.toLong(), RoleID = roleID, FromDate = getDateRange(filterItem.ID), ToDate = getTodayDate(),Filter = filter
                )
                viewModel.getTransactionHistory(requestModel)
            }

        }
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userID = it

        })
        viewModel.userRoleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

                val requestModel = CustomerRequestModel1(
                        UserID = userID.toLong(), RoleID = roleID, FromDate = getDateRange(DateTimeEnum.LAST_MONTH), ToDate = getTodayDate(),Filter = filter
                )
                viewModel.getTransactionHistory(requestModel)
            }

        })

        viewModel.transactionHistory.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        customerTransactionHistoryAdapter.setTransactionHistory(it)
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
        customerTransactionHistoryAdapter = CustomerTransactionHistoryAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = customerTransactionHistoryAdapter
        }
    }

    fun initialUiState() {
        binding.apply {

        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    override fun onTransactionHistoryItemClick(item: TransactionModel) {

    }
}