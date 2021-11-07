package com.sampurna.pocketmoney.shopping.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.shopping.viewmodel.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchProductActivity : AppCompatActivity() {
    private val filterViewModel: FilterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

    }
}