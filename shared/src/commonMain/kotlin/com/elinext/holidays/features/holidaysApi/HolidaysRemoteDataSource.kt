package com.elinext.holidays.features.holidaysApi

import com.elinext.holidays.models.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.Response

interface HolidaysRemoteDataSource {
    suspend fun getCountries(): Pair<HttpStatusCode,List<CountryModel>?>
    suspend fun getAllDays(): Pair<HttpStatusCode,Holidays?>
    suspend fun searchDay(date: String, id: String): Pair<HttpStatusCode,DayModel?>
    suspend fun getQuantityWorkingDays(year: String, id: String): Pair<HttpStatusCode,Number?>
}