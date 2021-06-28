package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityMyCouponsBinding
import com.example.pocketmoney.mlm.adapters.MyCouponsAdapter
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.ui.coupons.GenerateCoupon
import com.example.pocketmoney.mlm.viewmodel.MyCouponsViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyCoupons : BaseActivity<ActivityMyCouponsBinding>(ActivityMyCouponsBinding::inflate), ApplicationToolbar.ApplicationToolbarListener {

    // ViewModels
    private val viewModel by viewModels<MyCouponsViewModel>()
    // Adapters
    private lateinit var myCouponsAdapter: MyCouponsAdapter

    // List
    // Variable
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateTimeFilter(getTimeFilter().toMutableList())
        subscribeObservers()
        setupRecyclerView()

        binding.apply {
            toolbarMyCoupons.setApplicationToolbarListener(this@MyCoupons)
            fabCreateCoupon.setOnClickListener {
                val bottomSheet = GenerateCoupon()
                bottomSheet.show(supportFragmentManager,bottomSheet.tag)
            }
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


                    viewModel.getCouponList(userID,roleID,startDate,endDate)

                    //Do something...
                }

            } else {

                viewModel.getCouponList( userID, roleID,getDateRange(filterItem.ID),getTodayDate())
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

                viewModel.getCouponList(userID, roleID,getDateRange(DateTimeEnum.LAST_MONTH),getTodayDate())
            }

        })

        viewModel.couponList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        myCouponsAdapter.setCouponList(it)
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

    private fun setupRecyclerView() {
        myCouponsAdapter = MyCouponsAdapter()
        binding.rvCouponsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = myCouponsAdapter
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

}
