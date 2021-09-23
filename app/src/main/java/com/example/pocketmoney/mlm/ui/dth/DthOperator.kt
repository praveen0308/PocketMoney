package com.example.pocketmoney.mlm.ui.dth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentDthOperatorBinding
import com.example.pocketmoney.mlm.adapters.OperatorAdapter
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DthOperator : BaseFragment<FragmentDthOperatorBinding>(FragmentDthOperatorBinding::inflate), OperatorAdapter.OperatorAdapterInterface {

    private lateinit var operatorAdapter: OperatorAdapter


    private val viewModel by activityViewModels<DTHActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.getDTHOperators()

    }

    override fun subscribeObservers(){
        viewModel.dthOperators.observe(viewLifecycleOwner, {
            operatorAdapter.setComponentList(it)
        })
    }



    private fun setupRecyclerView() {
        operatorAdapter = OperatorAdapter(this)
        binding.rvDthOperatorList.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                    layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter =  operatorAdapter
        }
    }



    override fun onOperatorClick(operator: ModelOperator) {
        viewModel.selectedOperator.postValue(operator)
        findNavController().navigate(R.id.action_dthOperator_to_dthRechargeHost)
    }
}