package com.example.pocketmoney.mlm.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityComplaintListBinding
import com.example.pocketmoney.mlm.adapters.CustomerComplaintListAdapter
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.mlm.viewmodel.CustomerViewModel
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.*
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComplaintList : AppCompatActivity(), ApplicationToolbar.ApplicationToolbarListener {


    //UI
    private lateinit var binding: ActivityComplaintListBinding
    private lateinit var progressBarHandler: ProgressBarHandler

    // ViewModels
    private val customerViewModel by viewModels<CustomerViewModel>()
    private val userAuthenticationViewModel by viewModels<AccountViewModel>()


    // Adapters
    private lateinit var customerComplaintListAdapter: CustomerComplaintListAdapter

    // List
// Variable
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBarHandler = ProgressBarHandler(this)
        populateTimeFilter(getTimeFilter().toMutableList())
        subscribeObservers()
        setupRecyclerView()

        binding.apply {
            toolbarComplaintList.setApplicationToolbarListener(this@ComplaintList)
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


                    customerViewModel.getComplaintList(userID, roleID, startDate, endDate)

                    //Do something...
                }

            } else {

                customerViewModel.getComplaintList(userID, roleID, getDateRange(filterItem.ID), getTodayDate())
            }

        }
    }


    private fun subscribeObservers() {
        userAuthenticationViewModel.userID.observe(this, {
            userID = it

        })
        userAuthenticationViewModel.roleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

                customerViewModel.getComplaintList(userID, roleID, getDateRange(DateTimeEnum.LAST_MONTH), getTodayDate())
            }

        })

        customerViewModel.complaintList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        customerComplaintListAdapter.setComplaintList(it)
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

    private fun setupRecyclerView() {
        customerComplaintListAdapter = CustomerComplaintListAdapter()
        binding.rvComplaintList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = customerComplaintListAdapter
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
