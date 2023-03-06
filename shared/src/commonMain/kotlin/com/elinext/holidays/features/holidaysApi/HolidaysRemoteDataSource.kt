package com.elinext.holidays.features.holidaysApi

import com.elinext.holidays.models.CountryModel

interface HolidaysRemoteDataSource {
    suspend fun getCountries(): List<CountryModel>
}