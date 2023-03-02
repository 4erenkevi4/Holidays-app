package com.elinext.holidays.models


data class DayModel(
val comment: String,
val exclusionDate: String,
val type: String,
val version: Int,
val country: CountryModel
)

data class CountryModel(

var id: Int,
var name: String
)

data class HolidaysErrorModel(
    val rawMessage: String,
    val responseErrorCode: Int
)






