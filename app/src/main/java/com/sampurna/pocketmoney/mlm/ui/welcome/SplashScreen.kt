package com.sampurna.pocketmoney.mlm.ui.welcome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.sampurna.pocketmoney.databinding.FragmentSplashScreenBinding
import com.sampurna.pocketmoney.mlm.viewmodel.SplashScreenViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Constants
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2000)
                    performNavigation()
//                    findNavController().navigate(SplashScreenDirections.actionSplashScreenToOnBoardingScreen())
                }

            }
        }.addOnFailureListener {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                performNavigation()
//                    findNavController().navigate(SplashScreenDirections.actionSplashScreenToOnBoardingScreen())
            }

        }

/*
        Handler(Looper.getMainLooper()).postDelayed({
            performNavigation()
        }, 2000)*/
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