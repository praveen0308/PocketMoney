package com.example.pocketmoney.mlm.ui.welcome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pocketmoney.databinding.FragmentSplashScreenBinding
import com.example.pocketmoney.mlm.viewmodel.SplashScreenViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {

    private val viewModel by viewModels<SplashScreenViewModel>()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        Handler(Looper.getMainLooper()).postDelayed({
            performNavigation()
        }, 2000)
    }

    private fun performNavigation() {
        viewModel.welcomeStatus.observe(viewLifecycleOwner, {

            when(it){
                Constants.NEW_USER -> navController.navigate(SplashScreenDirections.actionSplashScreenToOnBoardingScreen())
                Constants.ONBOARDING_DONE -> navController.navigate(SplashScreenDirections.actionSplashScreenToLoginFragment())
                Constants.LOGIN_DONE -> {
                    navController.navigate(SplashScreenDirections.actionSplashScreenToMainDashboard())
                    requireActivity().finish()
                }

            }

        })
    }

    override fun subscribeObservers() {

    }

}