package com.example.pocketmoney.mlm.model

import androidx.datastore.preferences.core.preferencesKey

object MyPreferenceKeys {

    val isOnBoardingDone= preferencesKey<Boolean>("is_on_boarding_done")
    val welcomeStatus = preferencesKey<Int>("welcome_status")
    val loginId = preferencesKey<Int>("login_id")
    val userId = preferencesKey<String>("user_id")
    val userName = preferencesKey<String>("user_name")
    val userRoleId = preferencesKey<Int>("user_role_id")
}