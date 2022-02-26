package com.jmm.dth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.jmm.dth.databinding.ActivityDthBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class DthActivity : BaseActivity<ActivityDthBinding>(ActivityDthBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<DTHActivityViewModel>()
    private val mRequestCode = 100
    private lateinit var dthActivityListener :DthActivityInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbarDth.setApplicationToolbarListener(this)
//        val navController = findNavController(R.id.nav_host_dth)
//        val bundle = Bundle()
//        bundle.putSerializable("operatorType",RechargeEnum.DTH)
//        navController.setGraph(R.navigation.nav_dth,SelectOperatorArgs(RechargeEnum.DTH).toBundle())
    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

    fun setDthRechargeActivityListener(dthActivityInterface:DthActivityInterface){
        this.dthActivityListener = dthActivityInterface
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == mRequestCode && data != null) {
//            showToast(data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"))
            Timber.d("Response from AppInvoke >>> ${data.getStringExtra("response")}")

            if (data.getStringExtra("response").isNullOrEmpty()){
                showToast("Transaction Cancelled !!!")
                viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)

            }else{

                val json = JSONObject(data.getStringExtra("response")!!)
                Timber.d("response from str: $json")

                dthActivityListener.onAppInvokeResponse(json,"appinvoke")

            }


        }
        else{
            viewModel.dthRechargePageState.postValue(DthRechargePageState.CancelledGateway)
        }

    }


}

interface DthActivityInterface{
    fun onAppInvokeResponse(response: JSONObject, invoke:String)
}