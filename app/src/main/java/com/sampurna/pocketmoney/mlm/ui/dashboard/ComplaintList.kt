package com.sampurna.pocketmoney.mlm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.common.ChatActivity
import com.sampurna.pocketmoney.databinding.ActivityComplaintListBinding
import com.sampurna.pocketmoney.mlm.adapters.CustomerComplaintListAdapter
import com.sampurna.pocketmoney.mlm.model.UniversalFilterItemModel
import com.sampurna.pocketmoney.mlm.model.mlmModels.CustomerComplaintModel
import com.sampurna.pocketmoney.mlm.viewmodel.ComplaintListViewModel
import com.sampurna.pocketmoney.utils.*
import com.sampurna.pocketmoney.utils.myEnums.DateTimeEnum
import com.google.android.material.datepicker.MaterialDatePicker
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
