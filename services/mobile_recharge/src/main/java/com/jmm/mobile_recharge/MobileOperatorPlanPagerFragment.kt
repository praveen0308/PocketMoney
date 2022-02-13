package com.jmm.mobile_recharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.mobile_recharge.databinding.FragmentMobileOperatorPlanPagerBinding
import com.jmm.mobile_recharge.model.MobileOperatorPlanPrcl
import com.jmm.mobile_recharge.model.fromMobileOperatorPlan
import com.jmm.model.serviceModels.MobileOperatorPlan
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
    private val rechargeViewModel by viewModels<MobileRechargeViewModel>()

    // Interface
    private lateinit var mobileOperatorPlanPagerInterface: MobileOperatorPlanPagerInterface

    //Variables
    private lateinit var planList : ArrayList<MobileOperatorPlan>
    private lateinit var planListPrcl : ArrayList<MobileOperatorPlanPrcl>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planList = arrayListOf()
        arguments?.let {
            planListPrcl = it.getParcelableArrayList(ARG_PARAM1)!!

            planListPrcl.forEach {plan->
                planList.add(plan.toMobileOperatorPlan())
            }

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
        fun newInstance(param1: ArrayList<MobileOperatorPlan>, mobileOperatorPlanPagerInterface: MobileOperatorPlanPagerInterface) =
            MobileOperatorPlanPagerFragment().apply {
                this.mobileOperatorPlanPagerInterface = mobileOperatorPlanPagerInterface
                arguments = Bundle().apply {
                    val items = ArrayList<MobileOperatorPlanPrcl>()
                      param1.forEach { items.add(fromMobileOperatorPlan(it)) }
                    putParcelableArrayList(ARG_PARAM1, items)
                }
            }
    }


    override fun onPlanClick(plan: MobileOperatorPlan) {

        mobileOperatorPlanPagerInterface.onPlanChosen(plan)
    }

    interface MobileOperatorPlanPagerInterface{
        fun onPlanChosen(plan: MobileOperatorPlan)
    }
}