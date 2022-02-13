package com.jmm.membership

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.jmm.membership.databinding.FragmentActivateAccUsingCouponBinding
import com.jmm.util.BaseBottomSheetDialogFragment
import com.jmm.util.LoadingButton

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
        viewModel.userId.observe(this, {
            userId = it
        })
        viewModel.couponPageState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is ActivateUsingCouponPageState.Idle -> {

                }
                is ActivateUsingCouponPageState.Loading -> {
                    binding.btnAction.setState(LoadingButton.LoadingStates.LOADING, msg = "Processing...")
                }
                is ActivateUsingCouponPageState.Valid -> {
                    dismiss()
                }
                is ActivateUsingCouponPageState.Invalid -> {
                    binding.btnAction.setState(LoadingButton.LoadingStates.RETRY, "Retry")
                }
                is ActivateUsingCouponPageState.ValidatingCustomerRegistration -> {
                    binding.btnAction.setState(LoadingButton.LoadingStates.LOADING, msg = "Validating...")
                }
            }


        })


    }


}