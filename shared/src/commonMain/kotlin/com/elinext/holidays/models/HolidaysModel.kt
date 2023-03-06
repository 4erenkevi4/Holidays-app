package com.elinext.holidays.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayModel(
    @SerialName("comment") val comment: String,
    @SerialName("exclusionDate") val exclusionDate: String,
    @SerialName("type") val type: String,
    @SerialName("version") val version: Int,
    @SerialName("country") val country: CountryModel
)

@Serializable
data class CountryModel(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String
)

data class HolidaysErrorModel(
    val rawMessage: String,
    val responseErrorCode: Int
)






