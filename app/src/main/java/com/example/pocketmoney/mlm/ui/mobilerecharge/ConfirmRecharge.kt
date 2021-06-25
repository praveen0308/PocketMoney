package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentConfirmRechargeBinding
import com.example.pocketmoney.databinding.FragmentSelectContactBinding
import com.example.pocketmoney.databinding.FragmentSelectRechargePlanBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.MyCustomToolbar
import com.example.pocketmoney.utils.ProgressBarHandler
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.getMobileOperatorLogo
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ConfirmRecharge : Fragment(), MyCustomToolbar.MyCustomToolbarListener {

    private var param1: String? = null
    private var param2: String? = null
    // UI
    private var _binding: FragmentConfirmRechargeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var progressBarHandler: ProgressBarHandler
    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val rechargeViewModel by activityViewModels<RechargeViewModel>()

    // Interface


    //Variables
    private val args by navArgs<ConfirmRechargeArgs>()
    private var numberOfTab: Int? = -1
    private var specialPlanList = mutableListOf<MobileOperatorPlan>()
    private lateinit var circle : String
    private lateinit var operator : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())
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
        _binding = FragmentConfirmRechargeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarConfirmRecharge.setCustomToolbarListener(this)
        subscribeObservers()
        rechargeViewModel.getCircleNOperatorOfMobileNo(args.mobileNumber)

    }

    private fun subscribeObservers() {
        rechargeViewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        circle = it.circle
                        operator = it.Operator

                        populateUiElements()
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })


    }

    private fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }


    private fun populateUiElements(){

        val operatorPlan = rechargeViewModel.getSelectedRechargePlan()
        binding.apply {
            tvRechargeAmount.text = "₹${operatorPlan.rs}"
            tvPlanDescription.text = operatorPlan.desc

        }


        binding.toolbarConfirmRecharge.apply {
            setToolbarLogo(getMobileOperatorLogo(operator))
            if (args.contactName.isNullOrEmpty()){
                setToolbarTitle(args.mobileNumber)
            }else setToolbarTitle(args.contactName)
            setToolbarSubtitle("${args.mobileNumber} - $circle")
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmRecharge().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }
}