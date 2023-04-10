package com.elinext.holidays.features.holidaysApi

import com.elinext.holidays.models.ApiErrorModel
import kotlinx.coroutines.flow.MutableSharedFlow


class HolidaysApiRepository(
    private val remoteDataSource: HolidaysRemoteDataSource
) {
    suspend fun getCountries() = remoteDataSource.getCountries()
    suspend fun getAllDays() = remoteDataSource.getAllDays()
    suspend fun getQuantityWorkingDays(year: String, id: String) = remoteDataSource.getQuantityWorkingDays(year, id)
    suspend fun searchDay(date: String, id: String) = remoteDataSource.searchDay(date, id)

}