package com.jmm.navigation

sealed class MainNavigationFlow {
    object HomeFlow : MainNavigationFlow()
    object ShoppingFlow : MainNavigationFlow()
    object PaymentHistoryFlow : MainNavigationFlow()
    object UserAccountFlow : MainNavigationFlow()
}