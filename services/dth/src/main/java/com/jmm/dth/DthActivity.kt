package com.jmm.dth

import android.os.Bundle
import androidx.activity.viewModels
import com.jmm.dth.databinding.ActivityDthBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthActivity : BaseActivity<ActivityDthBinding>(ActivityDthBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<DTHActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarDth.setApplicationToolbarListener(this)
//        val navController = findNavController(R.id.nav_host_dth)
//        val bundle = Bundle()
//        bundle.putSerializable("operatorType",RechargeEnum.DTH)
//        navController.setGraph(R.navigation.nav_dth,SelectOperatorArgs(RechargeEnum.DTH).toBundle())
    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}