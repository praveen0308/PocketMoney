package com.jmm.membership

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jmm.membership.databinding.FragmentUpgradeToProBinding
import com.jmm.util.BaseBottomSheetDialogFragment

class UpgradeToPro : BaseBottomSheetDialogFragment<FragmentUpgradeToProBinding>(FragmentUpgradeToProBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnActivate.setOnClickListener {
            val intent = Intent(requireActivity(), ActivateAccount::class.java)
            startActivity(intent)
            dismiss()
        }
    }
    override fun subscribeObservers() {

    }

}