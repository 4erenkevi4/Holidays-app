package com.elinext.holidays.features.holidaysApi

import com.elinext.holidays.models.CountryModel
import com.elinext.holidays.models.DayModel
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.models.Holidays
import okhttp3.Response

interface HolidaysRemoteDataSource {
    suspend fun getCountries(): List<CountryModel>
    suspend fun getAllDays(): Map<Int, List<Holiday>>
    suspend fun searchDay(date: String, id: String): DayModel
    suspend fun getQuantityWorkingDays(year: String, id: String): Number
}