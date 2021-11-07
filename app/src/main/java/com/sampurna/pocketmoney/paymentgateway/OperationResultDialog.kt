package com.sampurna.pocketmoney.paymentgateway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sampurna.pocketmoney.databinding.FragmentOperationResultDialogBinding
import com.sampurna.pocketmoney.mlm.model.OperationResultModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class OperationResultDialog(private val operationResultModel: OperationResultModel, private val operationResultDialogCallback: OperationResultDialogCallback) : DialogFragment(),
    ApplicationToolbar.ApplicationToolbarListener {
    private var _binding: FragmentOperationResultDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        setStyle(STYLE_NORMAL,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOperationResultDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle1.text = operationResultModel.title1

            if (operationResultModel.animationUrl==0){
                lavSuccess.visibility = View.INVISIBLE
                tvSymbol.visibility = View.VISIBLE
                tvSymbol.text = operationResultModel.title1.take(1)
            }else{
                lavSuccess.visibility = View.VISIBLE
                lavSuccess.setAnimation(operationResultModel.animationUrl)
                tvSymbol.visibility = View.INVISIBLE
            }

            tvAmount.text = "₹${operationResultModel.amount}"
            tvOperationStatus.text  = operationResultModel.status

            val SDF_DMYHMS = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
            tvTimeStamp.text  = SDF_DMYHMS.format(Date()).toString()

            tvTransactionId.text  = "Ref. ID : ${operationResultModel.transactionId}"

            btnContinue.setOnClickListener {
                operationResultDialogCallback.onResultDialogDismiss()
                dismiss()
            }

            toolbarDialog.setApplicationToolbarListener(this@OperationResultDialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OperationResultDialogCallback{
        fun onResultDialogDismiss()
    }

    override fun onToolbarNavClick() {
        operationResultDialogCallback.onResultDialogDismiss()
    }

    override fun onMenuClick() {

    }
}