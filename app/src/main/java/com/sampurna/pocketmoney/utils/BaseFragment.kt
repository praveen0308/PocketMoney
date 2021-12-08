package com.sampurna.pocketmoney.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.jmm.brsap.dialog_builder.NordanLoadingDialog
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.common.AuthInterceptorSheet
import com.sampurna.pocketmoney.mlm.model.OperationResultModel
import com.sampurna.pocketmoney.paymentgateway.OperationResultDialog


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    // UI
    private var _binding: VB? = null
    val binding get() = _binding!!
    lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var loadingDialog: Dialog
//    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler =
            ProgressBarHandler(requireActivity())

        loadingDialog = NordanLoadingDialog.createLoadingDialog(requireActivity(),"Loading")
    }

    override fun onPause() {
        super.onPause()
        displayLoading(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        displayLoading(false)
        loadingDialog.dismiss()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        snackbar = Snackbar.make(view, "No internet!!!", Snackbar.LENGTH_INDEFINITE)
        subscribeObservers()



    }


    abstract fun subscribeObservers()

    protected fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }

    protected fun displayRefreshing(loading: Boolean) {
//        binding.swipeRefreshLayout.isRefreshing = loading
    }

    protected fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    protected fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(parentFragmentManager,sheet.tag)
    }


    protected fun showAlertDialog(message: String?,dialogClickListener:DialogInterface.OnClickListener){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    fun showSuccessfulDialog(title:String,actionText:String="",isSuccess:Boolean=true,dialogListener: MyDialogListener){
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_successful_popup, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)


        val btnOkay = dialogView.findViewById<Button>(R.id.btn_action)
        val lav = dialogView.findViewById<LottieAnimationView>(R.id.lav_success)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)

        if (!isSuccess){
            lav.setAnimation(R.raw.error_animation)
        }
        tvTitle.text = title
        if (actionText.isNotEmpty()){
            btnOkay.text = actionText
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = this.display
            val displayMetrics = DisplayMetrics()
            display!!.getRealMetrics(displayMetrics)
            val density = resources.displayMetrics.density
            val dpHeight = displayMetrics.heightPixels / density
            val dpWidth = displayMetrics.widthPixels / density

        } else {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = resources.displayMetrics.density
            val dpHeight = outMetrics.heightPixels / density
            val dpWidth = outMetrics.widthPixels / density

        }*/

        var displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.7f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height =  WindowManager.LayoutParams.WRAP_CONTENT
        alertDialog.window!!.attributes = layoutParams

        btnOkay.setOnClickListener {
            alertDialog.dismiss()
            dialogListener.onDismiss()
        }
    }


    fun showFullScreenDialog(operationResultModel: OperationResultModel, operationResultDialogCallback: OperationResultDialog.OperationResultDialogCallback){
        val dialogFragment = OperationResultDialog(operationResultModel,operationResultDialogCallback)
        dialogFragment.show(parentFragmentManager, "dialog")
    }


    fun showLoadingDialog(msg: String="Processing..."){
        loadingDialog.dismiss()
        loadingDialog = NordanLoadingDialog.createLoadingDialog(requireActivity(),msg)
        loadingDialog.show()
    }
    fun hideLoadingDialog(){
        loadingDialog.hide()
    }



}