package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentTransactionDetailBinding
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetail : BaseBottomSheetDialogFragment<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    override fun subscribeObservers() {

    }


}