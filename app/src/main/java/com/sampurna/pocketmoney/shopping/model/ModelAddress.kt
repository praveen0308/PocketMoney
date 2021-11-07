package com.sampurna.pocketmoney.shopping.model

data class ModelAddress(
    val AddedBy: Int?=null,
    val AddedOn: String?=null,
    val Address1: String?=null,
    val Address2: String?=null,
    val AddressID: Int=0,
    val AddressType: String?=null,
    val Building: String?=null,
    val CityID: String?=null,
    val CityName: String?=null,
    val CountryID: String?=null,
    val CountryName: String?=null,
    val District: String?=null,
    val IsCancel: Boolean?=null,
    val IsShipingEnable: Boolean?=null,
    val Locality: String?=null,
    val MobileNo: String?=null,
    val ModifiedBy: Int?=null,
    val ModifiedOn: String?=null,
    val Name: String?=null,
    val PostalCode: String?=null,
    val StateID: String?=null,
    val StateName: String?=null,
    val Street: String?=null,
    val UserID: String,
    var isSelected:Boolean?=false
)