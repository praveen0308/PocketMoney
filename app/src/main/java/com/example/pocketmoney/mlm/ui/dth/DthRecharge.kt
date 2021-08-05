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

class DthRecharge : BaseFragment<FragmentDthRechargeBinding>(FragmentDthRechargeBinding::inflate),
    MyCustomToolbar.MyCustomToolbarListener {

    private val viewModel by activityViewModels<DTHActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarDthRecharge.setCustomToolbarListener(this)
    }
    override fun subscribeObservers() {
        viewModel.selectedOperator.observe(viewLifecycleOwner,{
            binding.toolbarDthRecharge.setToolbarLogo(it.imageUrl as Int)
            binding.toolbarDthRecharge.setToolbarTitle(it.name.toString())
        })
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }

}