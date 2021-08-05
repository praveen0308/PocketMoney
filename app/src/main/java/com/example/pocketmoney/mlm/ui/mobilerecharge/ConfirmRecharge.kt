package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.databinding.FragmentConfirmRechargeBinding
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmRecharge : BaseFragment<FragmentConfirmRechargeBinding>(FragmentConfirmRechargeBinding::inflate), MyCustomToolbar.MyCustomToolbarListener {

    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val viewModel by activityViewModels<MobileRechargeViewModel>()

    // Interface


    //Variables
    private val args by navArgs<ConfirmRechargeArgs>()
    private var numberOfTab: Int? = -1
    private var specialPlanList = mutableListOf<MobileOperatorPlan>()
    private lateinit var circle : String
    private lateinit var operator : String

    private lateinit var mContact: ModelContact

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarConfirmRecharge.setCustomToolbarListener(this)
        subscribeObservers()
//        viewModel.getCircleNOperatorOfMobileNo(args.mobileNumber)

        binding.btnChangePlan.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun subscribeObservers() {
        viewModel.selectedContact.observe(viewLifecycleOwner, {
            mContact = it
//            viewModel.getCircleNOperatorOfMobileNo(mContact.contactNumber!!)
            populateUiElements()
        })
//        viewModel.circleNOperatorOfMobileNo.observe(viewLifecycleOwner, { _result ->
//            when (_result.status) {
//                Status.SUCCESS -> {
//                    _result._data?.let {
//                        circle = it.circle!!
//                        operator = it.Operator!!
//
//                        populateUiElements()
//                    }
//                    displayLoading(false)
//                }
//                Status.LOADING -> {
//                    displayLoading(true)
//                }
//                Status.ERROR -> {
//                    displayLoading(false)
//                    _result.message?.let {
//                        displayError(it)
//                    }
//                }
//            }
//        })


    }

    private fun populateUiElements(){

        val operatorPlan = viewModel.getSelectedRechargePlan()
        binding.apply {
            tvRechargeAmount.text = "₹${operatorPlan.rs}"
            tvPlanDescription.text = operatorPlan.desc

        }

        binding.toolbarConfirmRecharge.apply {
            setToolbarLogo(getMobileOperatorLogo(mContact.operator!!))
            if (mContact.contactName.isNullOrEmpty()) {
                setToolbarTitle(mContact.contactNumber!!)
                setToolbarSubtitle("Prepaid - ${mContact.circle}")

            } else {
                setToolbarTitle(mContact.contactName!!)
                setToolbarSubtitle("${mContact.contactNumber} - ${mContact.circle}")
            }
        }
//        binding.toolbarConfirmRecharge.apply {
//            setToolbarLogo(getMobileOperatorLogo(operator))
//            if (args.contactName.isNullOrEmpty()){
//                setToolbarTitle(args.mobileNumber)
//            }else setToolbarTitle(args.contactName)
//            setToolbarSubtitle("${args.mobileNumber} - $circle")
//        }
    }


    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }
}