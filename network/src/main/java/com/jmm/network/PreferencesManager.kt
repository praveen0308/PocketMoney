package com.jmm.network

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("PocketMoneySp", Context.MODE_PRIVATE)

    fun updateAccessToken(token:String){
        with(sharedPref.edit()){
            putString(ACCESS_TOKEN,token)
            apply()
        }
    }

    fun storeRefreshToken(token:String){
        with(sharedPref.edit()){
            putString(REFRESH_TOKEN,token)
            apply()
        }
    }

    fun getAccessToken() = sharedPref.getString(ACCESS_TOKEN,"")

    fun getRefreshToken() = sharedPref.getString(REFRESH_TOKEN,"")

    companion object{
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }
}

