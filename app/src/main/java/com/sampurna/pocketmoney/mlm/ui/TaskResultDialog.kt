package com.sampurna.pocketmoney.mlm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentTaskResultDialogBinding
import com.sampurna.pocketmoney.utils.getTodayDate
import com.sampurna.pocketmoney.utils.myEnums.WalletType
import com.sampurna.pocketmoney.utils.setAmount

class TaskResultDialog : DialogFragment() {
    private var _binding: FragmentTaskResultDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var params : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        params =requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTaskResultDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            ivClose.setOnClickListener {
                requireActivity().finish()
            }
            btnContinue.setOnClickListener {
                requireActivity().finish()
            }
        }

        populateRechargeStatusUi(params.getInt(KEY_STATUS))
    }
    private fun populateRechargeStatusUi(status:Int){
        binding.apply {
            tvTitle.text = params.getString(KEY_HEADING)
            tvSubtitle.text = params.getString(KEY_SUBTITLE)
        }
        when(status){
            PENDING -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.Orange
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_baseline_warning_24)
                }
            }
            FAILURE -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.CreamyRed
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_baseline_error_24)
                }

            }
            SUCCESS -> {
                binding.apply {
                    upperLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.success
                        )
                    )
                    ivStatus.setImageResource(R.drawable.ic_round_check_24)
                }
            }
        }

        binding.apply {
            when (params.getInt(KEY_WALLET_TYPE_ID)) {
                WalletType.Wallet.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_wallet)
                    tvPaidUsing.text = "Paid using wallet"
                }
                WalletType.PCash.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_loan)
                    tvPaidUsing.text = "Paid using pcash"

                }
                WalletType.OnlinePayment.id -> {
                    ivPaidUsing.setImageResource(R.drawable.ic_paytm_logo)
                    tvPaidUsing.text = "Paid using gateway"
                }
            }
            tvCurrentBalance.text = params.getString(KEY_PAYMENT_STATUS)
            tvCurrentBalanceOf.text = "Payment status"
            tvTransactionOf.text = "₹${params.getString(KEY_AMOUNT)} Recharge"
            tvAmount.setAmount(params.getString(KEY_AMOUNT)!!)
            tvRecipient.text = params.getString(KEY_ACCOUNT_NUMBER).toString()
            tvDate.text = getTodayDate()
            tvRefId.text = "Ref ID:${params.getString(KEY_REF_ID)}"

        }

        binding.root.isVisible = true
    }

    companion object{
        const val KEY_HEADING="heading"
        const val KEY_SUBTITLE="subtitle"
        const val KEY_STATUS="status"
        const val KEY_WALLET_TYPE_ID="walletTypeId"
        const val KEY_PAYMENT_STATUS="paymentStatus"
        const val KEY_AMOUNT="amount"
        const val KEY_ACCOUNT_NUMBER="accountMobileNumber"
        const val KEY_REF_ID="refId"

        const val SUCCESS = 1
        const val FAILURE = 2
        const val PENDING = 3
    }
}

