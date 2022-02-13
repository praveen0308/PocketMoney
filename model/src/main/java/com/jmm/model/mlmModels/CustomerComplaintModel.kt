package com.jmm.model.mlmModels

data class CustomerComplaintModel(
    val ComplainID: String,
    val ComplainStatus: String,
    val ComplainerComment: String,
    val ID: Int,
    val RegisteredBy: Double,
    val RegisteredOn: String,
    val RequestID: String,
    val RespondedBy: Double,
    val RespondedOn: String,
    val ResponderComment: String,
    val Status: String
)