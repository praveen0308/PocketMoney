package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityCustomerCommissionBinding
import com.example.pocketmoney.mlm.adapters.GrowthCommissionAdapter
import com.example.pocketmoney.mlm.adapters.GrowthCommissionHistoryAdapter
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.model.mlmModels.*
import com.example.pocketmoney.mlm.viewmodel.CommissionViewModel
import com.example.pocketmoney.mlm.viewmodel.CustomerViewModel
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.shopping.model.ModelProductVariant
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerCommissionActivity : AppCompatActivity(),
        GrowthCommissionAdapter.GrowthCommissionAdapterInterface,
        GrowthCommissionHistoryAdapter.GrowthCommissionHistoryAdapterInterface,
        ApplicationToolbar.ApplicationToolbarListener {

    //UI
    private lateinit var binding: ActivityCustomerCommissionBinding
    private lateinit var progressBarHandler: ProgressBarHandler

    // ViewModels
    private val commissionViewModel by viewModels<CommissionViewModel>()
    private val customerViewModel by viewModels<CustomerViewModel>()
    private val userAuthenticationViewModel by viewModels<AccountViewModel>()


    // Adapters
    private lateinit var growthCommissionAdapter: GrowthCommissionAdapter
    private lateinit var growthCommissionHistoryAdapter: GrowthCommissionHistoryAdapter

    // List
    private lateinit var productVariantList: MutableList<ModelProductVariant>

    // Variable
    private var source: Int = 0
    private var userID: String = ""
    private var roleID: Int = 0
    private var selectedCommission: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerCommissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBarHandler = ProgressBarHandler(this)
        source = intent.getIntExtra("SOURCE", 0)
        populateTimeFilter(getTimeFilter().toMutableList())
        subscribeObservers()
        setupCommissionMenusRecyclerview()
        setupCommissionDataRecyclerview()

        binding.apply {

            toolbarCustomerCommission.setApplicationToolbarListener(this@CustomerCommissionActivity)

//            clCommissionMenus.setOnClickListener {
//                if (rvItemList.visibility == View.VISIBLE) {
//                    rvItemList.visibility = View.GONE
//                } else rvItemList.visibility = View.VISIBLE
//            }
        }
    }

    private fun populateTimeFilter(stateList: MutableList<UniversalFilterItemModel>) {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, stateList)
        //actv is the AutoCompleteTextView from your layout file
//        binding.actvTimeFilter.threshold = 1 //start searching for values after typing first character
//        binding.actvTimeFilter.setAdapter(arrayAdapter)
//        initialUiState()
//        binding.actvTimeFilter.setOnItemClickListener { parent, view, position, id ->
//
//            val filterItem = parent.getItemAtPosition(position) as UniversalFilterItemModel
//
//            if (filterItem.ID as DateTimeEnum == DateTimeEnum.CUSTOM) {
//                val materialDateBuilder =
//                        MaterialDatePicker.Builder.dateRangePicker()
//                materialDateBuilder.setTitleText("SELECT A DATE")
//
//                val materialDatePicker = materialDateBuilder.build()
//                materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
//
//                materialDatePicker.addOnPositiveButtonClickListener { selection ->
//                    val startDate = convertMillisecondsToDate(selection.first, "yyyy-MM-dd")
//                    val endDate = convertMillisecondsToDate(selection.second, "yyyy-MM-dd")
//                    val requestModel = GrowthComissionRequestModel(
//                            userID, roleID, startDate, endDate
//                    )
//                    commissionViewModel.getGrowthCommission(requestModel)
//
//                    //Do something...
//                }
//
//            } else {
//                val requestModel = GrowthComissionRequestModel(
//                        userID, roleID, getDateRange(filterItem.ID), getTodayDate()
//                )
//                commissionViewModel.getGrowthCommission(requestModel)
//            }
//
//        }
    }


    private fun subscribeObservers() {
        userAuthenticationViewModel.userID.observe(this, {
            userID = it

        })
        userAuthenticationViewModel.roleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

                    val requestModel = GrowthComissionRequestModel(
                            userID, roleID, getDateRange(DateTimeEnum.LAST_MONTH), getTodayDate()
                    )
                    commissionViewModel.getGrowthCommission(requestModel)

            }

        })

        customerViewModel.customerGrowth.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

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
        commissionViewModel.growthCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        growthCommissionAdapter.setCommissionMenuItemList(getCommissionMenuList(it))
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

    private fun setupCommissionMenusRecyclerview() {
        growthCommissionAdapter = GrowthCommissionAdapter(this)
//        binding.rvItemList.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(context)
//            adapter = growthCommissionAdapter
//        }
    }

    private fun setupCommissionDataRecyclerview() {
        growthCommissionHistoryAdapter = GrowthCommissionHistoryAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = growthCommissionHistoryAdapter
        }
    }

    override fun onItemClick(item: GrowthCommissionDataModel) {
        displayCommissionSelected(item)
        if (item.commissionDataList != null) {
            growthCommissionHistoryAdapter.setCommissionDataItemList(item.commissionDataList!!)
//            binding.emptyView.root.visibility = View.GONE
        } else {
            growthCommissionHistoryAdapter.setCommissionDataItemList(listOf())
//            binding.emptyView.root.visibility = View.VISIBLE

        }

//        binding.rvItemList.visibility = View.GONE

    }

    private fun displayCommissionSelected(commissionDataModel: GrowthCommissionDataModel) {
        binding.apply {
//            txtTitle.text = commissionDataModel.title
//            txtCount.text = "₹ ${commissionDataModel.count}"
        }
    }

    private fun getCommissionMenuList(response: GrowthCommissionResponse): List<GrowthCommissionDataModel> {
//        binding.clCommissionMenus.visibility = View.VISIBLE
        val commission = response.GrowthCommission
        val menuList = mutableListOf<GrowthCommissionDataModel>()

        menuList.add(
                GrowthCommissionDataModel(
                        0,
                        "Direct Commission",
                        commission.DirectCommCount,
                        response.DirectCommHistory,
                        true
                )
        )
        menuList.add(
                GrowthCommissionDataModel(
                        1,
                        "Update Commission",
                        commission.UpdateCommCount,
                        response.UpdateCommHistory
                )
        )
        menuList.add(
                GrowthCommissionDataModel(
                        2,
                        "Service Commission",
                        commission.ServiceCommCount,
                        response.ServiceCommHistory
                )
        )
        menuList.add(
                GrowthCommissionDataModel(
                        3,
                        "Shopping Commission",
                        commission.ShoppingCommCount,
                        response.ShoppingCommHistory
                )
        )

        displayCommissionSelected(menuList[0])
        growthCommissionHistoryAdapter.setCommissionDataItemList(menuList[0].commissionDataList!!)
        if (menuList[0].commissionDataList!!.isEmpty()) {
//            binding.emptyView.root.visibility = View.VISIBLE
        }
        return menuList
    }




    override fun onCommissionHistoryItemClick(item: CommissionHistoryModel) {

    }

    fun initialUiState() {
        binding.apply {
//            clCommissionMenus.visibility = View.GONE

        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

}