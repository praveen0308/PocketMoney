package com.example.pocketmoney.mlm.ui.dth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentDthOperatorBinding
import com.example.pocketmoney.databinding.FragmentOperatorListBinding
import com.example.pocketmoney.mlm.adapters.OperatorAdapter
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class DthOperator : Fragment(), OperatorAdapter.OperatorAdapterInterface {

    private lateinit var operatorAdapter: OperatorAdapter

    private var _binding: FragmentDthOperatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel : RechargeViewModel by viewModels()

    private lateinit var navController: NavController

    private var param1: String? = null
    private var param2: String? = null

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
        _binding = FragmentDthOperatorBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupRecyclerView()
        subscribeObservers()

        viewModel.getOperatorList("DTH")

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

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DthOperator().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onOperatorClick() {
        navController.navigate(R.id.action_dthOperator_to_dthRecharge)
    }
}