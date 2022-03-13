package com.jmm.mlm.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.jmm.mlm.databinding.FragmentCustomerSupportPageBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerSupportPage : BaseFragment<FragmentCustomerSupportPageBinding>(FragmentCustomerSupportPageBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCustomerSupport()
        setupAccountSupport()
    }

    private fun setupAccountSupport() {
        binding.layoutAccountSupport.apply {
            tvTitle.text = "Account Support"
            // mobile1
            btnCall1.setOnClickListener {
                makeCall("7071717744")
            }
            btnCall2.setOnClickListener {
                makeCall("7071717755")
            }

            // email
            btnAction1.setOnClickListener {
                sendEmail("sampurnaenterprises.pm@gmail.com")
            }

            // location
            btnAction2.setOnClickListener {
                navigateToLocation("H NO. 1489 Gala 9 Ground Floor Bandu Jadhav Bulding, Narpoli Bhiwandi, Thane-421305, MH")
            }

            // whatsapp
            btnAction3.setOnClickListener {
                openWhatsApp("8767404060")
            }
        }

    }
    private fun setupCustomerSupport() {

        binding.layoutCustomerSupport.apply {
            tvTitle.text = "Customer Support"
            // mobile1
            btnCall1.setOnClickListener {
                makeCall("7071717722")
            }
            btnCall2.setOnClickListener {
                makeCall("7071717733")
            }

            // email
            btnAction1.setOnClickListener {
                sendEmail("sampurnaenterprises.pm@gmail.com")
            }

            // location
            btnAction2.setOnClickListener {
                navigateToLocation("H NO. 1489 Gala 9 Graund Floor Bandu Jadhav Bulding, Narpoli Bhiwandi, Thane-421305, MH")
            }

            // whatsapp
            btnAction3.setOnClickListener {
                openWhatsApp("9168435483")
            }
        }
    }
    private fun makeCall(number:String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
    private fun sendEmail(emailID:String){
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.parse("mailto:$emailID")
        email.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        email.putExtra(Intent.EXTRA_TEXT, "My Email message")
        startActivity(email)
    }

    private fun navigateToLocation(address:String){
        val map = "http://maps.google.co.in/maps?q=$address"
//        val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
        startActivity(intent)
    }
    private fun openWhatsApp(num: String) {
        val isAppInstalled = appInstalledOrNot("com.whatsapp")
        if (isAppInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$num"))
            startActivity(intent)
        } else {
            // WhatsApp not installed show toast or dialog
            showToast("Whatsapp not installed!!!")
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = requireActivity().packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    override fun subscribeObservers() {

    }

}