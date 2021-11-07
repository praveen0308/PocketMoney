package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentBeneficiaryListBinding
import com.sampurna.pocketmoney.mlm.adapters.BeneficiaryListAdapter
import com.sampurna.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BeneficiaryList : BaseFragment<FragmentBeneficiaryListBinding>(FragmentBeneficiaryListBinding::inflate),
    BeneficiaryListAdapter.BeneficiaryListInterface {

    private val viewModel by activityViewModels<PayoutViewModel>()
    private var payoutType = 1

    private var customerNumber = ""

    private lateinit var beneficiaryListAdapter: BeneficiaryListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvData()
        binding.btnAddBeneficiary.setOnClickListener {
            when(payoutType){
                1->{
                    val bottomSheet = AddBankBeneficiary()
                    bottomSheet.show(childFragmentManager,bottomSheet.tag)
                }
                2->{
                    val bottomSheet = AddPaytmBeneficiary()
                    bottomSheet.show(childFragmentManager,bottomSheet.tag)
                }
                3->{
                    val bottomSheet = AddPaytmBeneficiary()
                    bottomSheet.show(childFragmentManager,bottomSheet.tag)
                }
            }
        }
    }
    override fun subscribeObservers() {
        viewModel.payoutType.observe(viewLifecycleOwner,{
            payoutType = it
            if (!customerNumber.isNullOrEmpty()){
                viewModel.getBeneficiaries(customerNumber,payoutType)
            }

        })

        viewModel.customerNumber.observe(viewLifecycleOwner,{
            customerNumber = it
            viewModel.getBeneficiaries(customerNumber,payoutType)
        })
        viewModel.beneficiaryDetails.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        beneficiaryListAdapter.setBeneficiaryList(it)
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

    private fun setupRvData(){
        beneficiaryListAdapter = BeneficiaryListAdapter(this)
        binding.rvBeneficiaryList.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter = beneficiaryListAdapter
        }

    }

    override fun onTransferClick(beneficiary: Beneficiary) {
        viewModel.selectedBeneficiary.postValue(beneficiary)
        val sheet = PayoutTransferMoney()
        sheet.show(parentFragmentManager,sheet.tag)
    }

    override fun onBeneficiaryClick(beneficiary: Beneficiary) {

    }

}