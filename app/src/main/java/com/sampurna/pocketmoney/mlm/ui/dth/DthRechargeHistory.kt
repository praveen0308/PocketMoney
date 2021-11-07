package com.sampurna.pocketmoney.mlm.ui.dth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentDthRechargeHistoryBinding
import com.sampurna.pocketmoney.mlm.adapters.RechargeHistoryAdapter
import com.sampurna.pocketmoney.mlm.model.serviceModels.RechargeHistoryModel
import com.sampurna.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
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