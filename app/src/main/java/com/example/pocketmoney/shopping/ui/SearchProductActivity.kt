package com.example.pocketmoney.shopping.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.shopping.viewmodel.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchProductActivity : AppCompatActivity() {
    private val filterViewModel: FilterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

    }
}