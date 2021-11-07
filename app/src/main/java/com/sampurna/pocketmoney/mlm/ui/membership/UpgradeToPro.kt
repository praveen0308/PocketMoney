package com.sampurna.pocketmoney.mlm.ui.membership

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sampurna.pocketmoney.databinding.FragmentUpgradeToProBinding
import com.sampurna.pocketmoney.utils.BaseBottomSheetDialogFragment

class UpgradeToPro : BaseBottomSheetDialogFragment<FragmentUpgradeToProBinding>(FragmentUpgradeToProBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnActivate.setOnClickListener {
            val intent = Intent(requireActivity(),ActivateAccount::class.java)
            startActivity(intent)
            dismiss()
        }
    }
    override fun subscribeObservers() {

    }

}