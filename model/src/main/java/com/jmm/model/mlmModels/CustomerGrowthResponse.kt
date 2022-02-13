package com.jmm.model.mlmModels

data class CustomerGrowthResponse(
    val GrowthData: GrowthData,
    val GrowthHistory: List<GrowthHistory>,
    val RenewalHistory: List<Any>,
    val UpdateHistory: List<UpdateHistory>
)