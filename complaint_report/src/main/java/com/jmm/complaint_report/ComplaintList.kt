package com.jmm.complaint_report

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.jmm.complaint_report.databinding.ActivityComplaintListBinding
import com.jmm.core.utils.convertMillisecondsToDate
import com.jmm.core.utils.getDateRange
import com.jmm.core.utils.getTimeFilter
import com.jmm.core.utils.getTodayDate
import com.jmm.model.UniversalFilterItemModel
import com.jmm.model.mlmModels.CustomerComplaintModel
import com.jmm.model.myEnums.DateTimeEnum
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComplaintList : BaseActivity<ActivityComplaintListBinding>(ActivityComplaintListBinding::inflate), ApplicationToolbar.ApplicationToolbarListener,
    CustomerComplaintListAdapter.CustomerComplaintAdapterInterface {


    // ViewModels
    private val viewModel by viewModels<ComplaintListViewModel>()

    // Adapters
    private lateinit var customerComplaintListAdapter: CustomerComplaintListAdapter

    // List
// Variable
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        populateTimeFilter(getTimeFilter().toMutableList())
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


                    viewModel.getComplaintList(userID, roleID, startDate, endDate)

                    //Do something...
                }

            } else {

                viewModel.getComplaintList(userID, roleID, getDateRange(filterItem.ID), getTodayDate())
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

                viewModel.getComplaintList(userID, roleID, getDateRange(DateTimeEnum.LAST_MONTH), getTodayDate())
            }

        })

        viewModel.complaintList.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        customerComplaintListAdapter.setComplaintList(it.reversed())
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
        customerComplaintListAdapter = CustomerComplaintListAdapter(this)
        binding.rvComplaintList.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
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

    override fun onViewComplaint(complaintModel: CustomerComplaintModel) {
        val intent = Intent(this, ChatActivity::class.java)
//        intent.putExtra("TransactionId",complaintModel.transactionId)
        intent.putExtra("ReferenceId",complaintModel.RequestID)
        intent.putExtra("ComplaintId",complaintModel.ComplainID)
        startActivity(intent)
    }

}
