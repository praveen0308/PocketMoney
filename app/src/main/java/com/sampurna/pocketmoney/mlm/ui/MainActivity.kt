package com.sampurna.pocketmoney.mlm.ui

import android.os.Bundle
import android.view.WindowManager
import com.sampurna.pocketmoney.databinding.ActivityMainBinding
import com.sampurna.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


    }

    override fun subscribeObservers() {

    }


}