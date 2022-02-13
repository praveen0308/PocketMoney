package com.jmm.mobile_recharge

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.mobile_recharge.databinding.FragmentSelectOperatorBinding
import com.jmm.model.ModelOperator
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectOperator :
    BaseFragment<FragmentSelectOperatorBinding>(FragmentSelectOperatorBinding::inflate),
    OperatorAdapter.OperatorAdapterInterface {


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

}