package com.elinext.holidays.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Holidays(
    @SerialName("years") var years: Map<Int, List<Holiday>>
)

@Serializable
data class Country(
    @SerialName("id") var countryId: Int,
    @SerialName("name") var countryName: String,
)

@Serializable
data class Holiday(
    @SerialName("comment") var comment: String,
    @SerialName("exclusionDate") var holidayDate: String,
    @SerialName("type") var holidayType: String,
    @SerialName("country") var country: Country,
){

    fun getMonth(): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val dateTime: OffsetDateTime = OffsetDateTime.parse(holidayDate, formatter)
        return dateTime.monthValue
    }
}
