package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import com.example.pocketmoney.databinding.ActivityActivateAccountBinding
import com.example.pocketmoney.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivateAccount : BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun subscribeObservers() {

    }
}