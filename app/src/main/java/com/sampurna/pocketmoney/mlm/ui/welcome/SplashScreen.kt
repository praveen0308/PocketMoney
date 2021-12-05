package com.sampurna.pocketmoney.mlm.ui.welcome

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.sampurna.pocketmoney.authentication.SignUp
import com.sampurna.pocketmoney.databinding.FragmentSplashScreenBinding
import com.sampurna.pocketmoney.mlm.viewmodel.SplashScreenViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SplashScreen :
    BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {

    private val viewModel by viewModels<SplashScreenViewModel>()

    private lateinit var navController: NavController
    private val MY_REQUEST_CODE: Int = 100
    private lateinit var appUpdateManager: AppUpdateManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        appUpdateManager = AppUpdateManagerFactory.create(requireContext())
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    MY_REQUEST_CODE
                )
            } else {
                checkForEntry()

            }
        }.addOnFailureListener {
            checkForEntry()

        }

    }

    private fun checkForEntry() {
        try {
            Firebase.dynamicLinks
                .getDynamicLink(requireActivity().intent)
                .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->

                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        Timber.d("Deep link received : $deepLink")

                        try {
                            var lParams = deepLink.toString()
                                .substring(deepLink.toString().lastIndexOf("/") + 1)
                            Timber.d("Lparams >> $lParams")
                            val lParams1 = lParams.split("=")
                            Timber.d("Lparams >> $lParams1")

                            val destination = lParams1[0]
                            Timber.d("Destination >> $destination")
                            val uParams = lParams1[1].split("-")
                            Timber.d("uParams >> $uParams")
                            when (destination) {
                                "refer" -> {
                                    viewModel.updateSponsorId(uParams[0])
                                    val userName = uParams[0].replace("_", " ")
                                    viewModel.updateSponsorName(userName)
                                    val intent = Intent(requireActivity(), SignUp::class.java)
                                    /*intent.putExtra("userId",uParams[0])
                                    intent.putExtra("userName",userName)*/
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                                else -> {
                                    Timber.e("Unknown link!!!")
                                }
                            }

                        } catch (e: Exception) {

                        }
                    }

                }
                .addOnFailureListener(requireActivity()) { e ->
                    Timber.w("getDynamicLink:onFailure $e")

                }
        } finally {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                performNavigation()

            }
        }


    }
    private fun performNavigation() {

        viewModel.welcomeStatus.observe(viewLifecycleOwner, {

            when (it) {
                Constants.NEW_USER -> navController.navigate(SplashScreenDirections.actionSplashScreenToOnBoardingScreen())
                Constants.ONBOARDING_DONE -> {
                    navController.navigate(SplashScreenDirections.actionSplashScreenToMainDashboard())
                    requireActivity().finish()
//                    navController.navigate(SplashScreenDirections.actionSplashScreenToLoginFragment())
                }
                Constants.LOGIN_DONE -> {
                    navController.navigate(SplashScreenDirections.actionSplashScreenToMainDashboard())
                    requireActivity().finish()

                }

            }

        })
    }

    override fun subscribeObservers() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Timber.e("Update flow failed! Result code: $resultCode")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    MY_REQUEST_CODE
                )
            }
        }
    }
}