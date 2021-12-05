package com.sampurna.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import com.sampurna.pocketmoney.databinding.ActivityAccountBinding
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseActivity
import com.sampurna.pocketmoney.utils.myEnums.NavigationEnum
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
            NavigationEnum.SHARE->{showFragment(ShareUs())
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