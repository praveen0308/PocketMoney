package com.jmm.add_money

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.jmm.add_money.databinding.FragmentPaymentStatusBinding
import com.jmm.util.BaseFullScreenDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentStatus(private val args: PaymentStatusArgs) : BaseFullScreenDialogFragment<FragmentPaymentStatusBinding>(FragmentPaymentStatusBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivPaymentStatus.apply {
                when(args.paymentStatus){

                    "SUCCESS"->{
                        setImageResource(R.drawable.ic_round_check_circle_24)
                        setColorFilter(ContextCompat.getColor(context,R.color.Green))
                    }
                    "PENDING"->{
                        setImageResource(R.drawable.ic_round_info_24)
                        setColorFilter(ContextCompat.getColor(context,R.color.Orange))
                    }

                    "FAILURE","FAILED"->{
                        setImageResource(R.drawable.ic_round_close_24)
                        setColorFilter(ContextCompat.getColor(context,R.color.Red))
                    }

                    "CANCELLED","CANCEL"->{
                        setImageResource(R.drawable.ic_round_close_24)
                        setColorFilter(ContextCompat.getColor(context,R.color.Red))
                    }

                }
            }

            tvPaymentStatus.text =args.paymentStatus
            tvPaymentAmount.text = "₹${args.amount}"
            tvPaymentAmount.text = args.msg

            btnContinue.setOnClickListener {
                dismiss()
                requireActivity().finish()
            }
        }
    }
    override fun subscribeObservers() {

    }

}

data class PaymentStatusArgs(
    val status:String,
    val paymentStatus:String,
    val amount:Double,
    val msg:String,
)