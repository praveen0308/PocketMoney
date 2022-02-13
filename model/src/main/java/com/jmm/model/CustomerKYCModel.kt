package com.jmm.model

data class CustomerKYCModel(
    val ActionedBy: Int? = null,
    val ActionedOn: String? = null,
    val AddedOn: String? = null,
    val AddressLine1: String? = null,
    val AddressLine2: String? = null,
    val City: String? = null,
    val DocumentFileType: String? = null,
    val DocumentName: String? = null,
    val DocumentNumber: String? = null,
    val DocumentPath: String? = null,
    val DocumentTypeID: Int? = null,
    val ID: Int? = null,
    val IsActive: Boolean? = null,
    val IsApproved: Boolean? = null,
    val IsRejected: Boolean? = null,
    val PinCode: Int? = null,
    val State: String? = null,
    val UserID: String? = null,
    val base64Img: String? = null
)