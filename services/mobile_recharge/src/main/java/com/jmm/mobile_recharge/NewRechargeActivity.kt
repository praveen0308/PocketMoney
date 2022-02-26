package com.jmm.mobile_recharge

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jmm.mobile_recharge.databinding.ActivityNewRechargeBinding
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber


@AndroidEntryPoint
class NewRechargeActivity : BaseActivity<ActivityNewRechargeBinding>(ActivityNewRechargeBinding::inflate){

    private val viewModel by viewModels<MobileRechargeViewModel>()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration
    private val mRequestCode = 100

    private lateinit var newRechargeActivityListener :NewRechargeActivityInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(this,R.id.nav_host_new_recharge)
        appBarConfiguration = AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()
        binding.toolbarActivityRecharge.setupWithNavController(navController,appBarConfiguration)

    }

    fun setToolbarVisibility(status:Boolean){
        binding.toolbarActivityRecharge.isVisible = status
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun subscribeObservers() {

    }

    fun setNewRechargeActivityListener(newRechargeActivityInterface:NewRechargeActivityInterface){
        this.newRechargeActivityListener = newRechargeActivityInterface
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == mRequestCode && data != null) {
//            showToast(data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"))
            Timber.d("Response from AppInvoke >>> ${data.getStringExtra("response")}")

            if (data.getStringExtra("response").isNullOrEmpty()){
//                showToast("Transaction Cancelled !!!")
                viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)

            }else{

                val json = JSONObject(data.getStringExtra("response")!!)
                Timber.d("response from str: $json")
                newRechargeActivityListener.onAppInvokeResponse(json,"appinvoke")


            }


        }
        else{
            viewModel.rechargePageState.postValue(MobileRechargePageState.CancelledGateway)
        }

    }


}
interface NewRechargeActivityInterface{
    fun onAppInvokeResponse(response:JSONObject,invoke:String)
}