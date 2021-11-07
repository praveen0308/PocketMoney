package com.sampurna.pocketmoney.mlm.ui.mobilerecharge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sampurna.pocketmoney.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileRechargeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_recharge)
    }
}