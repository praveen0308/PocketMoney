package com.jmm.mlm.ui

import android.os.Bundle
import com.jmm.mlm.databinding.ActivityAccountBinding
import com.jmm.model.myEnums.NavigationEnum
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : BaseActivity<ActivityAccountBinding>(ActivityAccountBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val source  = intent.getSerializableExtra("SOURCE") as NavigationEnum
        binding.toolbarAccountActivity.setApplicationToolbarListener(this)
        when(source){
            NavigationEnum.ABOUT->{
                showFragment(AboutUs())
                binding.toolbarAccountActivity.setToolbarTitle("About Us")
            }
            NavigationEnum.SHARE->{
                showFragment(ShareUs())
                binding.toolbarAccountActivity.setToolbarTitle("Share Us")
            }
            else->{
                //nothing
            }
        }
    }

    override fun subscribeObservers() {

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}