package com.example.pocketmoney.mlm.ui.dth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityDthBinding
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.ui.mobilerecharge.SelectOperatorArgs
import com.example.pocketmoney.mlm.viewmodel.DTHActivityViewModel
import com.example.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthActivity : BaseActivity<ActivityDthBinding>(ActivityDthBinding::inflate) {

    private val viewModel by viewModels<DTHActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController(R.id.nav_host_dth)
//        val bundle = Bundle()
//        bundle.putSerializable("operatorType",RechargeEnum.DTH)
        navController.setGraph(R.navigation.nav_dth,SelectOperatorArgs(RechargeEnum.DTH).toBundle())
    }

    override fun subscribeObservers() {

    }
}