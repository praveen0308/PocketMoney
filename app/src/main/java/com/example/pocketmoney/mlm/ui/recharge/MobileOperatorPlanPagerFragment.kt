package com.example.pocketmoney.mlm.ui.recharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentEnterAmountBinding
import com.example.pocketmoney.databinding.FragmentMobileOperatorPlanPagerBinding
import com.example.pocketmoney.databinding.FragmentSelectRechargePlanBinding
import com.example.pocketmoney.mlm.adapters.OperatorPlanAdapter
import com.example.pocketmoney.mlm.model.ModelPlan
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.ui.mobilerecharge.SelectRechargePlanArgs
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.utils.ProgressBarHandler
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class MobileOperatorPlanPagerFragment : Fragment(), OperatorPlanAdapter.OperatorPlanAdapterListener {

    // UI
    private var _binding: FragmentMobileOperatorPlanPagerBinding? = null
    private val binding get() = _binding!!

    // Adapter
    private lateinit var operatorPlanAdapter: OperatorPlanAdapter

    // ViewModel
    private val rechargeViewModel by viewModels<RechargeViewModel>()

    // Interface
    private lateinit var mobileOperatorPlanPagerInterface: MobileOperatorPlanPagerInterface

    //Variables
    private lateinit var planList : ArrayList<MobileOperatorPlan>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            planList = it.getParcelableArrayList(ARG_PARAM1)!!
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentMobileOperatorPlanPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()
        if (planList.isEmpty()){
            binding.tvNotFound.visibility=View.VISIBLE
        }
        else{
            binding.tvNotFound.visibility=View.GONE
        }
    }

    private fun setupRecyclerview() {
        operatorPlanAdapter = OperatorPlanAdapter(planList,this)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = operatorPlanAdapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: ArrayList<MobileOperatorPlan>,mobileOperatorPlanPagerInterface: MobileOperatorPlanPagerInterface) =
                MobileOperatorPlanPagerFragment().apply {
                    this.mobileOperatorPlanPagerInterface = mobileOperatorPlanPagerInterface
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_PARAM1, param1)
                    }
                }
    }


    override fun onPlanClick(plan: MobileOperatorPlan) {

        mobileOperatorPlanPagerInterface.onPlanChosen(plan)
    }

    interface MobileOperatorPlanPagerInterface{
        fun onPlanChosen(plan:MobileOperatorPlan)
    }
}