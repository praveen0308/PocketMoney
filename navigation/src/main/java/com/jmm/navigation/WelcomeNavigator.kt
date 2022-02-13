package com.jmm.navigation

import android.app.Activity
import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelcomeNavigator @Inject constructor() {
    lateinit var navController: NavController
    lateinit var activity: Activity

    fun navigateToFlow(navigationFlow: MainNavigationFlow) {
        activity.runOnUiThread {
            with(navController) {
                when (navigationFlow) {
//                    NavigationFlow.AdminDashboardFlow -> navigate(MainNavGraphDirections.actionGlobalNoInternetFlow())

                }
            }
        }
    }

}