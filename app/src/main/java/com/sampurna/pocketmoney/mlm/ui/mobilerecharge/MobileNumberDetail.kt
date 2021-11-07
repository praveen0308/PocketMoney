package com.sampurna.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.activity.viewModels
import com.sampurna.pocketmoney.databinding.ActivityMobileNumberDetailBinding
import com.sampurna.pocketmoney.mlm.viewmodel.MobileNumberDetailViewModel
import com.sampurna.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileNumberDetail : BaseActivity<ActivityMobileNumberDetailBinding>(ActivityMobileNumberDetailBinding::inflate) {

    private val viewModel by viewModels<MobileNumberDetailViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val operator = intent.getStringExtra("operator")
        val circle = intent.getStringExtra("circle")

        viewModel.selectedOperator.postValue(operator)
        viewModel.selectedCircle.postValue(circle)
//        val navController = findNavController(R.id.nav_host_mobile_number_detail)
//        navController.setGraph(R.navigation.nav_mobile_number_detail,FragmentNumberDetailArgs(operator!!,circle!!).toBundle())
    }

    override fun subscribeObservers() {

    }
}