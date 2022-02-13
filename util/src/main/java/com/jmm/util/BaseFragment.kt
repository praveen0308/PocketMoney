package com.jmm.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.jmm.brsap.dialog_builder.NordanLoadingDialog
import com.jmm.model.OperationResultModel


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    // UI
    private var _binding: VB? = null
    val binding get() = _binding!!
    lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())
        loadingDialog = NordanLoadingDialog.createLoadingDialog(requireActivity(), "Loading")
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


    protected fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(parentFragmentManager,sheet.tag)
    }

    protected fun showAlertDialog(
        message: String?,
        positiveBtnClickListener: DialogInterface.OnClickListener,
        negativeBtnClickListener: DialogInterface.OnClickListener,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message).setPositiveButton("Yes", positiveBtnClickListener)
            .setNegativeButton("No", negativeBtnClickListener).show()
    }


    protected fun showAlertDialog(message: String?,dialogClickListener:DialogInterface.OnClickListener){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    fun showLoadingDialog(msg: String = "Processing...") {
        loadingDialog.dismiss()
        loadingDialog = NordanLoadingDialog.createLoadingDialog(requireActivity(), msg)
        loadingDialog.show()
    }

    fun showFullScreenDialog(operationResultModel: OperationResultModel, operationResultDialogCallback: OperationResultDialog.OperationResultDialogCallback){
        val dialogFragment = OperationResultDialog(
            operationResultModel,
            operationResultDialogCallback
        )
        dialogFragment.show(parentFragmentManager, "dialog")
    }


    fun hideLoadingDialog() {
        loadingDialog.hide()
    }

}