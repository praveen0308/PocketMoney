package com.jmm.model

data class CustomerDashboardDataModel(
    val BusinessWallet: Double= 0.0,
    val CustomerRank: Int =0,
    val DirectTeamCount: Int =0,
    val DownlineTeamCount: Int =0,
    val GrowthCount: Int =0,
    val IncomeWallet: Double =0.0,
    val RenewalCount: Int =0,
    val UpdateCount: Int =0
)