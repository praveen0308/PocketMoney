package com.jmm.shopping.ui

import android.os.Bundle
import com.jmm.shopping.databinding.ActivityProductListBinding
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductList : BaseActivity<ActivityProductListBinding>(ActivityProductListBinding::inflate) {


    private lateinit var type : String
    private var objectId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent.getStringExtra("Type").toString()
        objectId = intent.getIntExtra("ObjectId",0)

        when(type){
            "LatestProducts"->{

            }
            "FeaturedProducts"->{}
            "OfferedProducts"->{}
            "CategoryProducts"->{

            }
            "BrandProducts"->{}

        }
    }

    override fun subscribeObservers() {

    }
}