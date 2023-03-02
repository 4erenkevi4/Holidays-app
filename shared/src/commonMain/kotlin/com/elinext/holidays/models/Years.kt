package com.elinext.holidays.models


data class Holidays(

var years: Map<Int, List<Holiday>>
)

data class Country(

var id: Int,
 var name: String,
)

data class Holiday(
var comment: String,
var date: String,
var type: String,
var country: Country,
    )
