package com.example.pocketmoney.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.example.pocketmoney.R
import com.example.pocketmoney.common.AuthInterceptorSheet
import android.view.WindowManager

import android.util.DisplayMetrics
import android.view.Display
import android.widget.LinearLayout
import com.example.pocketmoney.mlm.model.OperationResultModel
import com.example.pocketmoney.paymentgateway.OperationResultDialog
import com.jmm.brsap.dialog_builder.NordanLoadingDialog


abstract class BaseActivity<B : ViewBinding>(private val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {
    lateinit var binding: B
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = NordanLoadingDialog.createLoadingDialog(this,"Loading")
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        subscribeObservers()
        progressBarHandler = ProgressBarHandler(this)

    }

    abstract fun subscribeObservers()

    protected fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }


    protected fun displayRefreshing(loading: Boolean) {

    }

    protected fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    protected fun showToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(supportFragmentManager,sheet.tag)
    }

    protected fun showAlertDialog(message: String?,dialogClickListener: DialogInterface.OnClickListener){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    protected fun showFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    fun showSuccessfulDialog(title:String,actionText:String="",dialogListener: MyDialogListener){
        val dialogBuilder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_successful_popup, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)


        val btnOkay = dialogView.findViewById<Button>(R.id.btn_action)
        val lav = dialogView.findViewById<LottieAnimationView>(R.id.lav_success)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)

        lav.animate()
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
        windowManager.defaultDisplay.getMetrics(displayMetrics)
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

    fun showFullScreenDialog(operationResultModel: OperationResultModel,operationResultDialogCallback: OperationResultDialog.OperationResultDialogCallback){
        val dialogFragment = OperationResultDialog(operationResultModel,operationResultDialogCallback)
        dialogFragment.show(supportFragmentManager, "dialog")
    }


    fun showLoadingDialog(msg: String="Processing..."){
        loadingDialog = NordanLoadingDialog.createLoadingDialog(this,msg)
        loadingDialog.show()
    }
    fun hideLoadingDialog(){
        loadingDialog.hide()
    }

}