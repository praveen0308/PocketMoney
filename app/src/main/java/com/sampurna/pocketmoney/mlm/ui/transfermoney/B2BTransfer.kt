package com.sampurna.pocketmoney.mlm.ui.transfermoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sampurna.pocketmoney.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class B2BTransfer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b2b_transfer)
    }
}