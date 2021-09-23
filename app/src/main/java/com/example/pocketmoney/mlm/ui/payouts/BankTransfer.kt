package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentBankTransferBinding
import com.example.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.example.pocketmoney.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankTransfer : BaseFragment<FragmentBankTransferBinding>(FragmentBankTransferBinding::inflate) {

    private val viewModel by activityViewModels<PayoutViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddBeneficiary.setOnClickListener {
            val bottomSheet = AddBankBeneficiary()
            bottomSheet.show(childFragmentManager,bottomSheet.tag)
        }
    }
    override fun subscribeObservers() {
        viewModel.customerNumber.observe(this, {
            binding.fabAddBeneficiary.isVisible = it.length==10
        })
    }

}