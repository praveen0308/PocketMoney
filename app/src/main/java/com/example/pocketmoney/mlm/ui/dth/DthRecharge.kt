package com.example.pocketmoney.mlm.ui.dth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentDthRechargeBinding
import com.example.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.MyCustomToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthRecharge : BaseFragment<FragmentDthRechargeBinding>(FragmentDthRechargeBinding::inflate){

    private val viewModel by activityViewModels<DTHActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun subscribeObservers() {

    }

}