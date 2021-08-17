package com.example.pocketmoney.mlm.ui.membership

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentUpgradeToProBinding
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment

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