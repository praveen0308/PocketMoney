package com.example.pocketmoney.mlm.ui.welcome

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pocketmoney.R
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : Fragment() {

    private val viewModel: AccountViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            performNavigation()
        }, 2000) // 300
    }

    private fun performNavigation() {
        viewModel.welcomeStatus.observe(viewLifecycleOwner, {
            Log.d("MyStatus", "performNavigation: $it")

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

}