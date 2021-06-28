package com.example.pocketmoney.mlm.ui.recharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentOperatorListBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.adapters.OperatorAdapter
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class OperatorList : Fragment(), OperatorAdapter.OperatorAdapterInterface {

    private var param1: String? = null
    private var param2: String? = null
    private var operatorType :String?=null
    val args: OperatorListArgs by navArgs()
    private lateinit var operatorAdapter: OperatorAdapter

    private var _binding: FragmentOperatorListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : RechargeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOperatorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operatorType = arguments?.getString("OPERATOR_TYPE")
//
//        when(operatorType){
//            "PREPAID","POSTPAID"->{
//                binding.mctilSearchOperator.input_edit_text.hint = "Select an operator"
//                binding.tvOperatorListTitle.text = getString(R.string.operators)
//            }
//            "DTH"->{
//                binding.mctilSearchOperator.input_edit_text.hint = "Select DTH Operator"
//                binding.tvOperatorListTitle.text = getString(R.string.dth_operators)
//            }
//            "ELECTRICITY"->{
//                binding.mctilSearchOperator.input_edit_text.hint = "Select an electricity board"
//                binding.tvOperatorListTitle.text = getString(R.string.electricity_boards)
//            }
//        }

        setupRecyclerView()
        subscribeObservers()

        viewModel.getOperatorList(operatorType!!)
    }

    private fun subscribeObservers(){
        viewModel.operatorList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ModelOperator>> -> {
                    displayLoading(false)
                    populateRecyclerView(dataState.data)
                }
                is DataState.Loading -> {
                    displayLoading(true)

                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayError(dataState.exception.message)
                }
            }
        })
    }


    private fun displayLoading(state: Boolean) {
//        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }


    private fun displayError(message: String?){
        if(message != null){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }


    private fun populateRecyclerView(data: List<ModelOperator>) {
        Timber.d("Response $data")
        operatorAdapter.setComponentList(data)

    }
    private fun setupRecyclerView() {
        operatorAdapter = OperatorAdapter(this)
        binding.rvOperatorList.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context,
                    layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter =  operatorAdapter
        }
    }

    override fun onOperatorClick() {

    }
}