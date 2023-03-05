package com.elinext.holidays.models
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class DayModel(
val comment: String,
val exclusionDate: String,
val type: String,
val version: Int,
val country: CountryModel
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






