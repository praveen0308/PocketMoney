package com.example.pocketmoney.shopping.model.orderModule

data class CustomerAddress(
    val AddedBy: Int,
    val AddedOn: String,
    val Address1: String,
    val Address2: String,
    val AddressID: Int,
    val AddressType: Any,
    val Building: String,
    val CityID: String,
    val CityName: Any,
    val CountryID: String,
    val CountryName: Any,
    val District: String,
    val IsCancel: Boolean,
    val IsShipingEnable: Boolean,
    val Locality: Any,
    val MobileNo: String,
    val ModifiedBy: Any,
    val ModifiedOn: Any,
    val Name: String,
    val PostalCode: String,
    val StateID: String,
    val StateName: Any,
    val Street: String,
    val UserID: String
)