package com.example.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentBankTransferBinding
import com.example.pocketmoney.databinding.FragmentWalletToPaytmTransferBinding
import com.example.pocketmoney.utils.BaseFragment


class WalletToPaytmTransfer : BaseFragment<FragmentWalletToPaytmTransferBinding>(FragmentWalletToPaytmTransferBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddBeneficiary.setOnClickListener {
            val bottomSheet = AddPaytmBeneficiary()
            bottomSheet.show(childFragmentManager,bottomSheet.tag)
        }
    }
    override fun subscribeObservers() {

    }

}