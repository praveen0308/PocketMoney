package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityCustomerGrowthBinding
import com.example.pocketmoney.mlm.adapters.GrowthCommissionAdapter
import com.example.pocketmoney.mlm.adapters.GrowthCommissionHistoryAdapter
import com.example.pocketmoney.mlm.model.mlmModels.CommissionHistoryModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerGrowthResponse
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.model.mlmModels.GrowthCommissionDataModel
import com.example.pocketmoney.mlm.viewmodel.CustomerGrowthActivityViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerGrowthActivity : BaseActivity<ActivityCustomerGrowthBinding>(ActivityCustomerGrowthBinding::inflate),
        GrowthCommissionAdapter.GrowthCommissionAdapterInterface,
        GrowthCommissionHistoryAdapter.GrowthCommissionHistoryAdapterInterface,
        ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<CustomerGrowthActivityViewModel>()

    // Adapters
    private lateinit var growthCommissionAdapter: GrowthCommissionAdapter
    private lateinit var growthCommissionHistoryAdapter: GrowthCommissionHistoryAdapter


    // Variable
    private var userID: String = ""
    private var roleID: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCommissionMenusRecyclerview()
        setupCommissionDataRecyclerview()

        binding.toolbarCustomerGrowth.setApplicationToolbarListener(this)


    }


    override fun subscribeObservers() {
        viewModel.userID.observe(this, {
            userID = it

        })
        viewModel.roleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

                val requestModel = CustomerRequestModel1(
                        UserID = userID.toLong(), RoleID = roleID, FromDate = getDateRange(DateTimeEnum.LAST_MONTH), ToDate = getTodayDate()
                )
                viewModel.getCustomerGrowth(requestModel)
            }

        })

        viewModel.customerGrowth.observe(this, { _result ->
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

    private fun setupCommissionMenusRecyclerview() {
        growthCommissionAdapter = GrowthCommissionAdapter(this)
        binding.rvCustomerGrowth.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = growthCommissionAdapter
        }
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

    }


    private fun getCommissionMenuList(response: CustomerGrowthResponse): List<GrowthCommissionDataModel> {

        val growthData = response.GrowthData
        val menuList = mutableListOf<GrowthCommissionDataModel>()
//
//        menuList.add(
//                GrowthCommissionDataModel(
//                        0,
//                        "System",
//                    "Growth",
//                        growthData.GrowthCount.toDouble(),
//                        response.GrowthHistory,
//                        true
//                )
//        )
//        menuList.add(
//                GrowthCommissionDataModel(
//                        1,
//                        "Renewal Growth",
//                    "Growth",
//                        growthData.RenewalCount.toDouble(),
//                        response.RenewalHistory
//                )
//        )
//        menuList.add(
//                GrowthCommissionDataModel(
//                        2,
//                        "Update Count",
//                    "Growth",
//                        growthData.UpdateCount.toDouble(),
//                        response.UpdateHistory
//                )
//        )

        growthCommissionHistoryAdapter.setCommissionDataItemList(menuList[0].commissionDataList!!)
        if (menuList[0].commissionDataList!!.isEmpty()) {
            binding.emptyView.isVisible
        }
        return menuList
    }

    override fun onCommissionHistoryItemClick(item: CommissionHistoryModel) {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

}