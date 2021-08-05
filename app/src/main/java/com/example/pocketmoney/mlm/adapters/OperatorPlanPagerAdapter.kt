package com.example.pocketmoney.mlm.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketmoney.mlm.model.ModelOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.ui.mobilerecharge.MobileOperatorPlanPagerFragment

//import com.example.pocketmoney.mlm.ui.recharge.MobileOperatorPlanPagerFragment

class OperatorPlanPagerAdapter(
    private val numberOfTabs: Int,
    private val operatorPlanList:List<ModelOperatorPlan>,
    fm: FragmentManager,
    lifecycle: Lifecycle,
    val mobileOperatorPlanPagerInterface: MobileOperatorPlanPagerFragment.MobileOperatorPlanPagerInterface
) : FragmentStateAdapter(fm,lifecycle){

    var fragment: Fragment? = null


    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        for (i in 0..numberOfTabs){
            if (i == position){
                fragment = MobileOperatorPlanPagerFragment.newInstance(
                        if (operatorPlanList[position].plansList!!.isEmpty()) arrayListOf()
                        else operatorPlanList[position].plansList as ArrayList<MobileOperatorPlan>,mobileOperatorPlanPagerInterface)
                break
            }
        }
        return fragment!!
    }
}