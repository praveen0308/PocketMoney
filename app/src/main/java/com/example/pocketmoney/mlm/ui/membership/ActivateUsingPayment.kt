package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentActivateUsingWalletBinding
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivateUsingPayment : BaseBottomSheetDialogFragment<FragmentActivateUsingWalletBinding>(FragmentActivateUsingWalletBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun subscribeObservers() {

    }


}