package com.jmm.mlm.ui

import com.jmm.mlm.databinding.FragmentAboutUsBinding
import com.jmm.util.BaseFragment

class AboutUs : BaseFragment<FragmentAboutUsBinding>(FragmentAboutUsBinding::inflate) {

    override fun onResume() {
        super.onResume()
//        (activity as MainDashboard).toggleBottomNav(false)
    }
    override fun subscribeObservers() {

    }

}