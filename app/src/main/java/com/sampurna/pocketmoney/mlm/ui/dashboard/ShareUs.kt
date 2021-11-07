package com.sampurna.pocketmoney.mlm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sampurna.pocketmoney.BuildConfig.APPLICATION_ID
import com.sampurna.pocketmoney.databinding.FragmentShareUsBinding
import com.sampurna.pocketmoney.utils.BaseFragment

class ShareUs : BaseFragment<FragmentShareUsBinding>(FragmentShareUsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShare.setOnClickListener{
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=$APPLICATION_ID"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    override fun subscribeObservers() {

    }

}