package com.jmm.network

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
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
        Timber.d("Updated access token!!!")
    }

    fun updateRefreshToken(token:String){
        with(sharedPref.edit()){
            putString(REFRESH_TOKEN,token)
            apply()
        }
        Timber.d("Updated refresh token!!!")
    }

    fun updateTokenExpiry(expiry:String){
        try {
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzz", Locale.US)
            format.timeZone = TimeZone.getTimeZone("GMT")

            val date = format.parse(expiry)
            val millis: Long = date!!.time
            with(sharedPref.edit()){
                putLong(EXPIRY_TIME_STAMP,millis)
                apply()
            }
            Timber.d("Updated token expiry!!!")
        }catch (e:Exception){
            Timber.e(e)

        }

    }
    fun getAccessToken() = sharedPref.getString(ACCESS_TOKEN,"")
    private fun getTokenExpiry() = sharedPref.getLong(EXPIRY_TIME_STAMP,0L)

    fun getRefreshToken() = sharedPref.getString(REFRESH_TOKEN,"")

    companion object{
        const val ACCESS_TOKEN = "access_token"
        const val EXPIRY_TIME_STAMP = "expiry_time_stamp"
        const val REFRESH_TOKEN = "refresh_token"
    }

    fun isTokenExpired():Boolean{
        val expiry = getTokenExpiry()
        val mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        val currentTime = mCalendar.timeInMillis

        val diff = currentTime - expiry

        return diff>0L || diff==0L

    }
}

