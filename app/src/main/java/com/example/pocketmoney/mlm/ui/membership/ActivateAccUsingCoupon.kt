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
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.LoadingButton
import com.example.pocketmoney.utils.LoadingButton.LoadingStates
import com.example.pocketmoney.utils.Status
import com.paytm.pgsdk.PaytmOrder

class ActivateAccUsingCoupon : BaseBottomSheetDialogFragment<FragmentActivateAccUsingCouponBinding>(FragmentActivateAccUsingCouponBinding::inflate) {

    private val viewModel by activityViewModels<ActivateAccountViewModel>()

    private val mobileNo = "0000000000"
    private var userId = ""
    private lateinit var pinNo : String
    private lateinit var pinSerial : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnAction.setButtonClick {

                if(etCouponPin.text.toString().isNotEmpty()){
                    if (etSerialNo.text.toString().isNotEmpty()){
                        pinNo = etCouponPin.text.toString().trim()
                        pinSerial = etSerialNo.text.toString().trim()
                        viewModel.validateCustomerRegistration(mobileNo,pinNo,pinSerial)
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
        viewModel.userId.observe(this,{
            userId = it
        })

        viewModel.isValid.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.activateAccountUsingCoupon(userId,pinNo, pinSerial)
                    }
                }
                Status.LOADING -> {
                    binding.btnAction.setState(LoadingStates.LOADING,msg = "Validating...")
                }
                Status.ERROR -> {
                    binding.btnAction.setState(LoadingStates.RETRY,"Retry")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

        viewModel.isActivationSuccessful.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        showToast("Account activated successfully !!!")
                        requireActivity().finish()
                    }
                }
                Status.LOADING -> {
                    binding.btnAction.setState(LoadingStates.LOADING,msg = "Activating...")
                }
                Status.ERROR -> {
                    binding.btnAction.setState(LoadingStates.RETRY,"Retry")
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

    }


}