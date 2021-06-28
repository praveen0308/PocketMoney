package com.example.pocketmoney.mlm.ui.customergrowth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.FragmentGrowthNCommissionListBinding
import com.example.pocketmoney.mlm.adapters.CustomerUpdateCountAdapter
import com.example.pocketmoney.mlm.adapters.GrowthNCommissionHistoryAdapter
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.model.mlmModels.CustomerRequestModel1
import com.example.pocketmoney.mlm.model.mlmModels.GrowthComissionRequestModel
import com.example.pocketmoney.mlm.model.mlmModels.UpdateHistory
import com.example.pocketmoney.mlm.viewmodel.CustomerGrowthNCommissionViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.FilterEnum
import com.example.pocketmoney.utils.myEnums.NavigationEnum
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GrowthNCommissionList :
    BaseFragment<FragmentGrowthNCommissionListBinding>(FragmentGrowthNCommissionListBinding::inflate),
    GrowthNCommissionHistoryAdapter.GrowthNCommissionHistoryInterface {

    private val args by navArgs<GrowthNCommissionListArgs>()
    private var source: NavigationEnum? = null

    // Adapters
    private lateinit var growthNCommissionHistoryAdapter: GrowthNCommissionHistoryAdapter
    private lateinit var updateCountAdapter: CustomerUpdateCountAdapter

    private val viewModel by viewModels<CustomerGrowthNCommissionViewModel>()

    // Variables
    private var startDate: String = getDateRange(FilterEnum.LAST_MONTH)
    private var endDate: String = getTodayDate()
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = args.source
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateTimeFilter(getTimeFilters().toMutableList())
        setupRvGrowthNCommission()

    }

    private fun setupRvGrowthNCommission() {
        growthNCommissionHistoryAdapter = GrowthNCommissionHistoryAdapter(this)

        binding.rvData.apply {
            isVisible = true
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = growthNCommissionHistoryAdapter
        }
    }

    private fun setupSlUpdateCount(items: List<UpdateHistory>) {
        binding.slUpdateCount.isVisible = true
        updateCountAdapter = CustomerUpdateCountAdapter(items)
        binding.slUpdateCount.setAdapter(updateCountAdapter)
    }

    override fun subscribeObservers() {
        viewModel.userID.observe(this, {
            userID = it

        })
        viewModel.roleID.observe(this, {
            roleID = it
        })
        viewModel.selectedTimeFilter.observe(viewLifecycleOwner, {
            when (it) {
                FilterEnum.LAST_MONTH, FilterEnum.LAST_WEEK, FilterEnum.YESTERDAY, FilterEnum.TODAY -> {
                    startDate = getDateRange(it)
                    endDate = getTodayDate()
                    fetchDataList()

//                    binding.tvDateInfo.text = getDateLabelAcToFilter(it)
                }
                FilterEnum.TOMORROW, FilterEnum.THIS_WEEK, FilterEnum.THIS_MONTH -> {
                    startDate = getTodayDate()
                    endDate = getDateRange(it)
                    fetchDataList()
//                    binding.tvDateInfo.text = getDateLabelAcToFilter(it)
                }

                FilterEnum.CUSTOM -> {
                    val materialDateBuilder =
                        MaterialDatePicker.Builder.dateRangePicker()
                    materialDateBuilder.setTitleText("SELECT A DATE")

                    val materialDatePicker = materialDateBuilder.build()
                    materialDatePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")

                    materialDatePicker.addOnPositiveButtonClickListener { selection ->
                        startDate = convertMillisecondsToDate(selection.first, "dd MMM,yyyy")
                        endDate = convertMillisecondsToDate(selection.second, "dd MMM,yyyy")
//                        binding.tvDateInfo.text = "From $startDate to $endDate"
                        fetchDataList()
                    }
                }
            }

        })

        viewModel.customerGrowth.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        when (source) {
                            NavigationEnum.SYSTEM_GROWTH -> {
                                if (it.GrowthHistory.isNotEmpty()) {
                                    growthNCommissionHistoryAdapter.setAnyList(it.GrowthHistory)
                                    binding.layoutNoData.isVisible = false
                                }
                                else {
                                    binding.layoutNoData.apply {
                                        growthNCommissionHistoryAdapter.setAnyList(it.GrowthHistory)
                                        isVisible = true
                                        setMessage("No growth !!!")
                                    }

                                }
                            }

                            NavigationEnum.UPDATE_COUNT -> {
                                if (it.UpdateHistory.isNotEmpty()){
                                    setupSlUpdateCount(it.UpdateHistory)
                                    binding.layoutNoData.isVisible = false
                                }
                                else {
                                    binding.layoutNoData.apply {
                                        
                                        isVisible = true
                                        setMessage("No rank updated !!!")
                                    }

                                }
                            }
                            NavigationEnum.RENEWAL_COUNT -> {

                            }

                            else -> {
                                //nothing
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

        viewModel.customerDirectCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isNotEmpty()){
                            growthNCommissionHistoryAdapter.setAnyList(it)
                            binding.layoutNoData.isVisible = false
                        }
                        else {
                            binding.layoutNoData.apply {
                                growthNCommissionHistoryAdapter.setAnyList(it)
                                isVisible = true
                                setMessage("No records !!!")
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

        viewModel.customerServiceCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isNotEmpty()){
                            growthNCommissionHistoryAdapter.setAnyList(it)
                            binding.layoutNoData.isVisible = false
                        }
                        else {
                            binding.layoutNoData.apply {
                                growthNCommissionHistoryAdapter.setAnyList(it)
                                isVisible = true
                                setMessage("No records !!!")
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

        viewModel.customerUpdateCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isNotEmpty()){
                            growthNCommissionHistoryAdapter.setAnyList(it)
                            binding.layoutNoData.isVisible = false
                        }
                        else {
                            binding.layoutNoData.apply {
                                growthNCommissionHistoryAdapter.setAnyList(it)
                                isVisible = true
                                setMessage("No records !!!")
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

        viewModel.customerShoppingCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it.isNotEmpty()){
                            growthNCommissionHistoryAdapter.setAnyList(it)
                            binding.layoutNoData.isVisible = false
                        }
                        else {
                            binding.layoutNoData.apply {
                                growthNCommissionHistoryAdapter.setAnyList(it)
                                isVisible = true
                                setMessage("No records !!!")
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

    private fun fetchDataList(){
        val commissionRequestModel = GrowthComissionRequestModel(
            UserID = userID, RoleID = roleID, FromDate = startDate, ToDate = endDate
        )
        when (source) {

            NavigationEnum.SYSTEM_GROWTH, NavigationEnum.UPDATE_COUNT, NavigationEnum.RENEWAL_COUNT -> {
                viewModel.getCustomerGrowth(
                    CustomerRequestModel1(
                        UserID = userID.toLong(),
                        RoleID = roleID,
                        FromDate = startDate,
                        ToDate = endDate
                    )
                )
            }
            NavigationEnum.DIRECT_COMMISSION -> viewModel.getCustomerDirectCommission(
                commissionRequestModel
            )
            NavigationEnum.SERVICE_COMMISSION -> viewModel.getCustomerServiceCommission(
                commissionRequestModel
            )
            NavigationEnum.UPDATE_COMMISSION -> viewModel.getCustomerUpdateCommission(
                commissionRequestModel
            )
            NavigationEnum.SHOPPING_COMMISSION -> viewModel.getCustomerShoppingCommission(
                commissionRequestModel
            )

            else -> {
                // nothing
            }
        }
    }

    override fun onItemClick(item: Any) {

    }

    private fun populateTimeFilter(stateList: MutableList<UniversalFilterItemModel>) {
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, stateList)
        binding.actvTimeFilter.threshold = 1 //start searching for values after typing first character
        binding.actvTimeFilter.setAdapter(arrayAdapter)

        binding.actvTimeFilter.setOnItemClickListener { parent, view, position, id ->

            val filterItem = parent.getItemAtPosition(position) as UniversalFilterItemModel
            viewModel.assignSelectedTimeFilter(filterItem.ID as FilterEnum)
        }
    }


}