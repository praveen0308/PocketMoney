package com.example.pocketmoney.mlm.ui.recharge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.databinding.ActivityOperatorsPlanBinding
import com.example.pocketmoney.mlm.adapters.OperatorPlanPagerAdapter
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.mlm.model.ModelPlan
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OperatorsPlan : AppCompatActivity() {

    private lateinit var binding:ActivityOperatorsPlanBinding
    var numberOfTab: Int? = -1
    private lateinit var operatorPlanList: MutableList<ModelOperatorPlan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorsPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getOperatorPlanList()
        numberOfTab = operatorPlanList.size-1
        for (i in 0..numberOfTab!!){
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(operatorPlanList[i].title))
        }
//        val pagerViewAdapter = OperatorPlanPagerAdapter(binding.tabLayout.tabCount,operatorPlanList,supportFragmentManager,lifecycle,this)
//       binding.viewPager.adapter = pagerViewAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = operatorPlanList[position].title
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }


    private fun getOperatorPlanList(){
        operatorPlanList = ArrayList()

//        val planList = mutableListOf<ModelPlan>()
//
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//        planList.add(ModelPlan(2.5,"5 months","5 GB",200))
//
//
//
//        operatorPlanList.add(ModelOperatorPlan("4G",planList))
//        operatorPlanList.add(ModelOperatorPlan("Topup",planList))
//        operatorPlanList.add(ModelOperatorPlan("Special Offer",planList))
//        operatorPlanList.add(ModelOperatorPlan("SMS",planList))
//        operatorPlanList.add(ModelOperatorPlan("Data",planList))
//        operatorPlanList.add(ModelOperatorPlan("Data",planList))
//        operatorPlanList.add(ModelOperatorPlan("Data",planList))


    }

}