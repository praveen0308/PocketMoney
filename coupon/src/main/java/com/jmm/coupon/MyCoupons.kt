package com.jmm.coupon

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.jmm.core.utils.convertMillisecondsToDate
import com.jmm.core.utils.getDateRange
import com.jmm.core.utils.getTimeFilter
import com.jmm.core.utils.getTodayDate
import com.jmm.coupon.databinding.FragmentMyCouponsBinding
import com.jmm.model.UniversalFilterItemModel
import com.jmm.model.myEnums.DateTimeEnum
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyCoupons : BaseFragment<FragmentMyCouponsBinding>(FragmentMyCouponsBinding::inflate){

    // ViewModels
    private val viewModel by viewModels<MyCouponsViewModel>()
    // Adapters
    private lateinit var myCouponsAdapter: MyCouponsAdapter

    // List
    // Variable
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateTimeFilter(getTimeFilter().toMutableList())
        setupRecyclerView()

        binding.apply {

            fabCreateCoupon.setOnClickListener {
                findNavController().navigate(MyCouponsDirections.actionMyCouponsToGenerateCoupon2())
            }
        }
    }

    private fun populateTimeFilter(stateList: MutableList<UniversalFilterItemModel>) {
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, stateList)
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
                materialDatePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")

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

    override fun onResume() {
        super.onResume()
        if (userID != "" && roleID != 0) {
            viewModel.getCouponList(userID, roleID,getDateRange(DateTimeEnum.LAST_MONTH),getTodayDate())
        }

    }
    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userID = it

        }
        viewModel.userRoleID.observe(this) {
            roleID = it
            if (userID != "" && roleID != 0) {

                viewModel.getCouponList(
                    userID,
                    roleID,
                    getDateRange(DateTimeEnum.LAST_MONTH),
                    getTodayDate()
                )
            }

        }

        viewModel.couponList.observe(this) { _result ->
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
        }

    }

    private fun setupRecyclerView() {
        myCouponsAdapter = MyCouponsAdapter()
        binding.rvCouponsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = myCouponsAdapter
        }
    }



    private fun initialUiState() {
        binding.apply {

        }
    }


}
