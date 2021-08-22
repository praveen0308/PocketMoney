package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pocketmoney.R
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityActivateAccountBinding
import com.example.pocketmoney.mlm.viewmodel.ActivateAccountViewModel
import com.example.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivateAccount : BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate) {

    private val viewModel by viewModels<ActivateAccountViewModel>()

    private var selectedMethod = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnActivate.setOnClickListener {
            when(selectedMethod){
                1->{
                    val sheet = ActivateAccUsingCoupon()
                    sheet.show(supportFragmentManager,sheet.tag)
                }
                2->{
                    val sheet = PaymentMethods()
                    sheet.show(supportFragmentManager,sheet.tag)
                }
            }
        }

        binding.rgPaymentChoices.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_coupon->{
                    selectedMethod = 1
                }
                R.id.rb_payment->{
                    selectedMethod = 2
                }
            }
        }
    }

    override fun subscribeObservers() {
        lifecycleScope.launch {
//            viewModel.selectedMethod.collect {
//                selectedMethod= it
//            }
        }

    }


}