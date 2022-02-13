package com.jmm.model.mlmModels

data class GrowthCommissionResponse(
    val DirectCommHistory: List<CommissionHistoryModel>?= listOf(),
    val GrowthCommission: GrowthCommission,
    val ServiceCommHistory: List<CommissionHistoryModel>?= listOf(),
    val ShoppingCommHistory: List<CommissionHistoryModel>?= listOf(),
    val UpdateCommHistory: List<CommissionHistoryModel>?= listOf()
)