package com.jmm.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
abstract class BaseBottomSheetDialogFragment<VB: ViewBinding>(
        private val inflate: Inflate<VB>
) : BottomSheetDialogFragment() {

    // UI
    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())

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

    protected fun checkAuthorization(){
        val sheet = AuthInterceptorSheet()
        sheet.show(parentFragmentManager,sheet.tag)
    }

    protected fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    protected fun showToast(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }



}