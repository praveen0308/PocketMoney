package com.jmm.repository

import android.content.Context
import com.jmm.model.CustomerDashboardDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserAccountData @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val sharedPrefs =
        context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getWalletBalance() = sharedPrefs.getFloat(WALLET_BALANCE, 0f).toDouble()
    fun getPCashBalance() = sharedPrefs.getFloat(P_CASH, 0f).toDouble()
    fun getDownline() = sharedPrefs.getInt(DOWNLINE, 0)
    fun getDirectTeam() = sharedPrefs.getInt(DIRECT_TEAM, 0)


    fun updateWalletBalance(value: Double) {
        with(sharedPrefs.edit()) {
            putFloat(WALLET_BALANCE, value.toFloat())
            apply()
        }

    }

    fun updatePCash(value: Double) {
        with(sharedPrefs.edit()) {
            putFloat(P_CASH, value.toFloat())
            apply()
        }
    }

    fun updateDownLine(value: Int) {
        with(sharedPrefs.edit()) {
            putInt(DOWNLINE, value)
            apply()
        }
    }

    fun updateDirectTeam(value: Int) {
        with(sharedPrefs.edit()) {
            putInt(DIRECT_TEAM, value)
            apply()
        }
    }
    fun updateCustomerAccountData(data:CustomerDashboardDataModel){
        updateWalletBalance(data.BusinessWallet)
        updatePCash(data.IncomeWallet)
        updateDownLine(data.DownlineTeamCount)
        updateDirectTeam(data.DirectTeamCount)
    }
    fun getCustomerDashboardData(): CustomerDashboardDataModel{

        return CustomerDashboardDataModel(
            BusinessWallet = getWalletBalance(),
            IncomeWallet = getPCashBalance(),
            DownlineTeamCount = getDownline(),
            DirectTeamCount = getDirectTeam()
        )
    }

    suspend fun clearUserAccountData() {
        updateWalletBalance(0.0)
        updatePCash(0.0)
        updateDownLine(0)
        updateDirectTeam(0)

        delay(1000)
    }


    companion object {
        const val USER_PREFERENCES_NAME = "UserAccountData"

        const val WALLET_BALANCE = "wallet_balance"
        const val P_CASH = "pcash_balance"
        const val DOWNLINE = "downline"
        const val DIRECT_TEAM = "direct_team"


    }
}