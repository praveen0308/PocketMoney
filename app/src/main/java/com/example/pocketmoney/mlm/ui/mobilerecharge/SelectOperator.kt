package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSelectOperatorBinding
import com.example.pocketmoney.mlm.adapters.OperatorAdapter
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.example.pocketmoney.mlm.viewmodel.MobileNumberDetailViewModel
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.MyCustomToolbar
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.setAmount
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectOperator :
    BaseFragment<FragmentSelectOperatorBinding>(FragmentSelectOperatorBinding::inflate),
    OperatorAdapter.OperatorAdapterInterface, MyCustomToolbar.MyCustomToolbarListener {

    private val dthActivityViewModel by activityViewModels<DTHActivityViewModel>()
//    private val mobileRechargeViewModel by activityViewModels<MobileRechargeViewModel>()
private val viewModel by activityViewModels<MobileRechargeViewModel>()

    private lateinit var operatorAdapter: OperatorAdapter

//    private val args by navArgs<SelectOperatorArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvOperators()

        viewModel.getMobileOperators()

    }

    override fun subscribeObservers() {

        viewModel.mobileOperators.observe(viewLifecycleOwner, {
            operatorAdapter.setComponentList(it)
        })

    }

    private fun setupRvOperators() {
        operatorAdapter = OperatorAdapter(this)
        binding.rvDthOperatorList.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                layoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = operatorAdapter
        }
    }

    override fun onOperatorClick(operator: ModelOperator) {
        viewModel.selectedOperator.postValue(operator.name)
        findNavController().navigateUp()
      /*  when (args.operatorType) {
            RechargeEnum.PREPAID, RechargeEnum.POSTPAID -> {
                mobileNumberDetailViewModel.selectedOperator.postValue(operator.name)
                findNavController().popBackStack()
            }
            RechargeEnum.DTH -> {
                dthActivityViewModel.selectedOperator.postValue(operator)
//                findNavController().navigate(R.id.action_selectOperator_to_dthRecharge)
            }
        }*/

    }

    override fun onToolbarNavClick() {

    }

    override fun onMenuClick() {

    }
}