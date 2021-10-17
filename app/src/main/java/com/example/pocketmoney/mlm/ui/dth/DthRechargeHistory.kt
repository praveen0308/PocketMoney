package com.example.pocketmoney.mlm.ui.dth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentDthRechargeHistoryBinding
import com.example.pocketmoney.mlm.adapters.PaymentHistoryAdapter
import com.example.pocketmoney.mlm.adapters.RechargeHistoryAdapter
import com.example.pocketmoney.mlm.model.serviceModels.RechargeHistoryModel
import com.example.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthRechargeHistory : BaseFragment<FragmentDthRechargeHistoryBinding>(FragmentDthRechargeHistoryBinding::inflate),
    RechargeHistoryAdapter.RechargeHistoryInterface {

    private val viewModel by activityViewModels<DTHActivityViewModel>()
    private lateinit var rechargeHistoryAdapter:RechargeHistoryAdapter

    private lateinit var userId: String
    private var roleId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvHistory()

    }

    private fun setupRvHistory(){
        rechargeHistoryAdapter = RechargeHistoryAdapter(this)
        binding.rvRechargeHistory.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter = rechargeHistoryAdapter
        }

    }
    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userId = it
        })

        viewModel.userRoleID.observe(this, {
            roleId = it
            val requestData = JsonObject()
            requestData.addProperty("UserID",userId)
            requestData.addProperty("RoleID",roleId)
            requestData.addProperty("ServiceTypeID",2)
            requestData.addProperty("Filter","RECENT")
            viewModel.getRechargeHistory(requestData)
        })

        viewModel.rechargeHistory.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        rechargeHistoryAdapter.setRechargeHistoryModelList(it)

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

    override fun onRechargeClick(item: RechargeHistoryModel) {
        viewModel.rechargeMobileNumber.postValue(item.MobileNo?.substring(0,item.MobileNo.length-2))
        viewModel.rechargeAmount.postValue(item.RechargeAmt!!.toInt())
        viewModel.currentActivePage.postValue(0)
    }


}