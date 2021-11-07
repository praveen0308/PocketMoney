package com.sampurna.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentSelectOperatorBinding
import com.sampurna.pocketmoney.mlm.adapters.OperatorAdapter
import com.sampurna.pocketmoney.mlm.model.ModelOperator
import com.sampurna.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.sampurna.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.MyCustomToolbar
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