package com.jmm.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : DialogFragment() {

    // UI
    private var _binding: VB? = null
    val binding get() = _binding!!
    lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
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
    }

    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(parentFragmentManager,sheet.tag)
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

    protected fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showAlertDialog(message: String?,dialogClickListener:DialogInterface.OnClickListener){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

}