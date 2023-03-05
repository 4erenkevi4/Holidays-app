package com.elinext.holidays.models

@kotlinx.serialization.Serializable
data class Day(
    val day: Int,
    val month: Int,
    val year: Int,
    val date: String,
    val isHoliday: Boolean,
    val description: String? = null,
)

data class Month(
    var year: Int,
    var month: Int,
    var listOfDays: MutableList<Day?>,
)

data class Year(
    var year: Int,
    var listOfMonth:MutableList<Month>
)