package com.jmm.shopping.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jmm.shopping.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

    }
}