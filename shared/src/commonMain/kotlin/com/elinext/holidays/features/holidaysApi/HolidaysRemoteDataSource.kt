package com.elinext.holidays.features.holidaysApi

import com.elinext.holidays.models.CountryModel
import com.elinext.holidays.models.DayModel
import com.elinext.holidays.models.Holidays
import io.ktor.http.HttpStatusCode

interface HolidaysRemoteDataSource {
    suspend fun getCountries(): Pair<HttpStatusCode,List<CountryModel>?>
    suspend fun getAllDays(): Pair<HttpStatusCode,Holidays?>
    suspend fun searchDay(date: String, id: String): Pair<HttpStatusCode,DayModel?>
    suspend fun getQuantityWorkingDays(year: String, id: String): Pair<HttpStatusCode,Number?>
}