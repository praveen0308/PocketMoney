package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.View
import com.sampurna.pocketmoney.databinding.FragmentWalletToPaytmTransferBinding
import com.sampurna.pocketmoney.utils.BaseFragment


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