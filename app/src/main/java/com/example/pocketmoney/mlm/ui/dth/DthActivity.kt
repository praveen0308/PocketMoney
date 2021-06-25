package com.example.pocketmoney.mlm.ui.dth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dth)
    }
}