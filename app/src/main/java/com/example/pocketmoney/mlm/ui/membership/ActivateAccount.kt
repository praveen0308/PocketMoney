package com.example.pocketmoney.mlm.ui.membership

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityActivateAccountBinding
import com.example.pocketmoney.utils.BaseActivity

class ActivateAccount : BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activate_account)
    }

    override fun subscribeObservers() {

    }
}