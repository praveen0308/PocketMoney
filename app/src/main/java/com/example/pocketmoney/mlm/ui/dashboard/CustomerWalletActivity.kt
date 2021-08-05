package com.example.pocketmoney.mlm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityCustomerWalletBinding
import com.example.pocketmoney.mlm.adapters.CustomerTransactionHistoryAdapter
import com.example.pocketmoney.mlm.model.TransactionModel
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.ui.transfermoney.B2BTransfer
import com.example.pocketmoney.mlm.viewmodel.CustomerWalletViewModel
import com.example.pocketmoney.shopping.model.ModelProductVariant
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.google.android.material.datepicker.MaterialDatePicker
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
        populateTimeFilter(getTimeFilter().toMutableList())
        setupRecyclerview()
        binding.toolbarCustomerWallet.setApplicationToolbarListener(this)
        binding.fabTransfer.setOnClickListener {
            startActivity(Intent(this, B2BTransfer::class.java))
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