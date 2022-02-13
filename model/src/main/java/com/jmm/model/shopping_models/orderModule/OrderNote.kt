package com.jmm.model.shopping_models.orderModule

data class OrderNote(
    val Attachments: Any,
    val CreatedDate: String,
    val DisplayCustomerInd: Boolean,
    val Id: Int,
    val IsActive: Boolean,
    val NoteText: String,
    val OrderNumber: String,
    val UpdatedUserId: String
)