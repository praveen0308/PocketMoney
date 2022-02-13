package com.jmm.model

data class ComplainModel(
    val ActionFlagID: Int? = null,
    val ComplainID: String? = null,
    val ComplainStatus: String? = null,
    val ComplainerComment: String? = null,
    val ID: Int? = null,
    val RegisteredBy: Double? = null,
    val RegisteredOn: String? = null,
    val RequestID: String? = null,
    val RespondedBy: Double? = null,
    val RespondedOn: String? = null,
    val ResponderComment: String? = null,
    val Status: Any? = null
)