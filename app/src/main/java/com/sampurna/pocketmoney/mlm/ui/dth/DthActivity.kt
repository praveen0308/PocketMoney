package com.sampurna.pocketmoney.mlm.ui.dth

import android.os.Bundle
import androidx.activity.viewModels
import com.sampurna.pocketmoney.databinding.ActivityDthBinding
//import com.sampurna.pocketmoney.mlm.ui.mobilerecharge.SelectOperatorArgs
import com.sampurna.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.sampurna.pocketmoney.utils.BaseActivity
import com.sampurna.pocketmoney.utils.MyCustomToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthActivity : BaseActivity<ActivityDthBinding>(ActivityDthBinding::inflate),
    MyCustomToolbar.MyCustomToolbarListener {

    private val viewModel by viewModels<DTHActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarDth.setCustomToolbarListener(this)
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