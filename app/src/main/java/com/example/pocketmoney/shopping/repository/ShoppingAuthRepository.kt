package com.example.pocketmoney.shopping.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import com.example.pocketmoney.mlm.model.MyPreferenceKeys
import com.example.pocketmoney.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ShoppingAuthRepository @Inject constructor(
    context: Context
) {

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = Constants.PREFERENCE_NAME
    )

    val userID: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val userId = preference[MyPreferenceKeys.userId]
            userId!!
        }

}