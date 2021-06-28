package com.example.pocketmoney.mlm.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityAccountBinding
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.myEnums.NavigationEnum

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