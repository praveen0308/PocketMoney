package com.jmm.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.jmm.brsap.dialog_builder.NordanLoadingDialog
import com.jmm.model.OperationResultModel


abstract class BaseActivity<B : ViewBinding>(private val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {
    lateinit var binding: B
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        subscribeObservers()
        progressBarHandler = ProgressBarHandler(this)
        loadingDialog = NordanLoadingDialog.createLoadingDialog(this, "Loading")
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
    }

    abstract fun subscribeObservers()

    protected fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }


    protected fun displayRefreshing(loading: Boolean) {

    }

    protected fun showFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
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

    protected fun showAlertDialog(
        message: String?,
        dialogClickListener: DialogInterface.OnClickListener
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }


    fun showLoadingDialog(msg: String = "Processing...") {
        loadingDialog.dismiss()
        loadingDialog = NordanLoadingDialog.createLoadingDialog(this, msg)
        loadingDialog.show()
    }

    fun hideLoadingDialog() {
        loadingDialog.hide()
    }
    fun showFullScreenDialog(operationResultModel: OperationResultModel, operationResultDialogCallback: OperationResultDialog.OperationResultDialogCallback){
        val dialogFragment = OperationResultDialog(
            operationResultModel,
            operationResultDialogCallback
        )
        dialogFragment.show(supportFragmentManager, "dialog")
    }


    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(supportFragmentManager,sheet.tag)
    }


}