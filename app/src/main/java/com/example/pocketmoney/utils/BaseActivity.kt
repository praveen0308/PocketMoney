package com.example.pocketmoney.utils

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.pocketmoney.R
import com.example.pocketmoney.common.AuthInterceptorSheet


abstract class BaseActivity<B : ViewBinding>(private val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {
    lateinit var binding: B
    private lateinit var progressBarHandler: ProgressBarHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

}