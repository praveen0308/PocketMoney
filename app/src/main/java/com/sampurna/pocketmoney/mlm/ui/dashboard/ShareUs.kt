package com.sampurna.pocketmoney.mlm.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sampurna.pocketmoney.BuildConfig.APPLICATION_ID
import com.sampurna.pocketmoney.databinding.FragmentShareUsBinding
import com.sampurna.pocketmoney.mlm.viewmodel.AccountViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShareUs : BaseFragment<FragmentShareUsBinding>(FragmentShareUsBinding::inflate) {

    private val viewModel by viewModels<AccountViewModel>()
    private lateinit var userId: String
    private lateinit var userName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShare.setOnClickListener {
            Firebase.dynamicLinks.shortLinkAsync {
                val fUsr = userName.replace(" ", "_")
                link = Uri.parse("https://www.pocketmoney.net.in/refer=$userId-$fUsr")
                domainUriPrefix = "https://app.pocketmoney.net.in/"
                androidParameters(APPLICATION_ID) {
                    minimumVersion = 1
                }
                socialMetaTagParameters {
                    imageUrl =
                        "https://firebasestorage.googleapis.com/v0/b/pocketmoney-5523d.appspot.com/o/pmm.png?alt=media&token=651d527a-5bc3-4f85-8b29-b4f800124933".toUri()
                    title = "Pocket Money"
                    description = "Recharge,Shopping & Bill Payment Application."
                }

            }.addOnSuccessListener { (shortLink, flowchartLink) ->
                Timber.d("Short link >>> $shortLink")
                // Short link created
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check out my app at: $shortLink"
                )
                /*sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=$APPLICATION_ID"
                )*/
                sendIntent.type = "text/plain"
                startActivity(sendIntent)

            }.addOnFailureListener {
                Timber.e(it)
            }

        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner, {
            userId = it
        })
        viewModel.userName.observe(viewLifecycleOwner, {
            userName = it
        })
    }


}