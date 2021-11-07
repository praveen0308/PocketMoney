package com.sampurna.pocketmoney.shopping.ui.customernavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sampurna.pocketmoney.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCategories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_categories)
    }
}