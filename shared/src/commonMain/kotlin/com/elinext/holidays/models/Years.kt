package com.elinext.holidays.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Holidays(
    @SerialName("years") var years: Map<Int, List<Holiday>>
)

@Serializable
data class Country(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String,
)

@Serializable
data class Holiday(
    @SerialName("comment") var comment: String,
    @SerialName("date") var date: String,
    @SerialName("type") var type: String,
    @SerialName("country") var country: Country,
)
