package com.jmm.core

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
import com.jmm.core.databinding.FragmentSplashScreenBinding
import com.jmm.core.utils.Constants
import com.jmm.lock_screen.FragmentPinView
import com.jmm.navigation.NavRoute.MainDashboard
import com.jmm.navigation.NavRoute.SignUp
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SplashScreen :
    BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {

    private val viewModel by viewModels<SplashScreenViewModel>()

    private lateinit var navController: NavController
    private val myRequestCode: Int = 100
    private lateinit var appUpdateManager: AppUpdateManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        checkForNewUpdate()
    }

    private fun checkForNewUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(requireContext())
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    myRequestCode
                )
            } else {
                // If update not available
                checkForEntry()
            }
        }.addOnFailureListener {
            // If app update checking failed caused by No internet or something else
            checkForEntry()

        }

    }


    /****
     * Checking for entry to detect that is user opened application from dynamic
     * referral link or normal
     *
     * ****/
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
                            /***
                             * Here to take only params after last /
                             * i.e
                             * www.example.com/user/refer=abcd-1234
                             * so we will get only string -> refer=abcd-1234
                             * ***/
                            val lParams = deepLink.toString()
                                .substring(deepLink.toString().lastIndexOf("/") + 1)
                            Timber.d("Lparams >> $lParams")

                            /***
                             * Splitting refer=abcd-1234
                             * into two parts : refer and abcd-1234
                             * named as destination refer
                             * and params abcd-1234 respectively
                             * ***/
                            val lParams1 = lParams.split("=")
                            Timber.d("Lparams >> $lParams1")

                            val destination = lParams1[0]
                            Timber.d("Destination >> $destination")

                            /***
                             * Splitting params(abcd-1234) by - this mark so that we get
                             * params abcd and 1234
                             * ***/
                            val uParams = lParams1[1].split("-")
                            Timber.d("uParams >> $uParams")

                            when (destination) {
                                "refer" -> {

                                    viewModel.updateSponsorId(uParams[0])
                                    val userName = uParams[0].replace("_", " ")
                                    viewModel.updateSponsorName(userName)
                                    val intent = Intent(requireActivity(), Class.forName(SignUp))
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
                            Timber.d("Exception raised while reading dynamic link")
                            Timber.d("Exception : $e")
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

        viewModel.welcomeStatus.observe(viewLifecycleOwner) {

            when (it) {
                Constants.NEW_USER -> navController.navigate(SplashScreenDirections.actionSplashScreenToOnboardingFlow())
                Constants.ONBOARDING_DONE -> {
                    startActivity(Intent(requireActivity(), Class.forName(MainDashboard)))
                    requireActivity().finish()

                }
                Constants.LOGIN_DONE -> {
                    /*** Check for pin authorisation ***/
                    val pinView = FragmentPinView()
                    pinView.show(parentFragmentManager, pinView.tag)
                    /*startActivity(Intent(requireActivity(), Class.forName(MainDashboard)))
                    requireActivity().finish()*/


                }

            }

        }
    }

    override fun subscribeObservers() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == myRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                Timber.d("Updated successfully !!!")
                performNavigation()
            } else {
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
                    myRequestCode
                )
            }
        }
    }

}