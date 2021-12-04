package com.sampurna.pocketmoney.mlm.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sampurna.pocketmoney.mlm.model.UserModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/*

Author : Praveen A. Yadav
Created On : 05:18 27-05-2021

*/

class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

    val welcomeStatus: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[WELCOME_STATUS] ?: NEW_USER
            pref
        }


    val loginId: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[LOGIN_ID] ?: 0
            pref
        }

    val userId: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[USER_ID] ?: ""
            pref
        }

    val userName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[USER_NAME] ?: ""
            pref
        }

    val userRoleId: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[USER_ROLE_ID] ?: 0
            pref
        }

    val firstName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[USER_FIRST_NAME] ?: ""
            pref
        }

    val lastName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[USER_LAST_NAME] ?: ""
            pref
        }

    val isBlocked: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[IS_BLOCKED] ?: false
            pref
        }

    val isActive: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // No type safety.
            val pref = preferences[IS_ACTIVE] ?: false
            pref
        }


    suspend fun updateWelcomeStatus(status: Int) {
        context.dataStore.edit { preference ->
            preference[WELCOME_STATUS] = status
        }
    }

    suspend fun updateLoginId(loginId: Int) {
        context.dataStore.edit { preference ->
            preference[LOGIN_ID] = loginId
        }
    }

    suspend fun updateUserId(userId: String) {
        context.dataStore.edit { preference ->
            preference[USER_ID] = userId
        }
    }

    suspend fun updateUserName(userName: String) {
        context.dataStore.edit { preference ->
            preference[USER_NAME] = userName
        }
    }

    suspend fun updateUserFirstName(userName: String) {
        context.dataStore.edit { preference ->
            preference[USER_FIRST_NAME] = userName
        }
    }

    suspend fun updateUserLastName(userName: String) {
        context.dataStore.edit { preference ->
            preference[USER_LAST_NAME] = userName
        }
    }

    suspend fun updateUserRoleId(userRoleId: Int) {
        context.dataStore.edit { preference ->
            preference[USER_ROLE_ID] = userRoleId
        }
    }

    suspend fun updateStatus(status: Boolean) {
        context.dataStore.edit { preference ->
            preference[IS_BLOCKED] = status
        }
    }

    suspend fun updateUserType(isActive: Boolean) {
        context.dataStore.edit { preference ->
            preference[IS_ACTIVE] = isActive
        }
    }

    suspend fun clearUserInfo() {
        updateLoginId(0)
        updateUserId("")
        updateUserName("")
        updateUserRoleId(0)
        updateUserFirstName("")
        updateUserLastName("")
        updateWelcomeStatus(1)
        updateStatus(false)
        updateUserType(false)
        updateWelcomeStatus(1)
        delay(1000)
    }

    suspend fun storeUserLoginInfo(userModel: UserModel) {

        context.dataStore.edit { preference ->
            preference[LOGIN_ID] = userModel.LoginID!!
            preference[USER_ID] = userModel.UserID!!
            preference[USER_NAME] = userModel.UserName!!
            preference[USER_ROLE_ID] = userModel.UserRoleID!!

        }
    }


    companion object {
        const val USER_PREFERENCES_NAME = "PocketMoney"

        const val NEW_USER = 0
        const val ON_BOARDING_DONE = 1
        const val LOGIN_DONE = 2

//        val IS_ON_BOARDING_DONE= booleanPreferencesKey("is_on_boarding_done")

        val WELCOME_STATUS = intPreferencesKey("welcome_status")

        val LOGIN_ID = intPreferencesKey("login_id")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE_ID = intPreferencesKey("user_role_id")

        val USER_FIRST_NAME = stringPreferencesKey("user_first_name")
        val USER_LAST_NAME = stringPreferencesKey("user_last_name")


        val IS_BLOCKED = booleanPreferencesKey("status")
        val IS_ACTIVE = booleanPreferencesKey("active")


    }
}


