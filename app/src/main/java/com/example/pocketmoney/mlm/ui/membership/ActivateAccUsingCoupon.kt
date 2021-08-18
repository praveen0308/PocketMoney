package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentActivateAccUsingCouponBinding
import com.example.pocketmoney.mlm.viewmodel.ActivateAccountViewModel
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment

class ActivateAccUsingCoupon : BaseBottomSheetDialogFragment<FragmentActivateAccUsingCouponBinding>(FragmentActivateAccUsingCouponBinding::inflate) {

    private val viewModel by activityViewModels<ActivateAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnConfirm.setOnClickListener {
                if(etCouponPin.text.toString().isNotEmpty()){
                    if (etSerialNo.text.toString().isNotEmpty()){
//                        viewModel.validateCustomerRegistration()
                    }
                    else{
                        showToast("Enter coupon series !!!")
                    }
                }else{
                    showToast("Enter coupon pin number !!!!")
                }
            }
        }

    }

    override fun subscribeObservers() {

    }


}