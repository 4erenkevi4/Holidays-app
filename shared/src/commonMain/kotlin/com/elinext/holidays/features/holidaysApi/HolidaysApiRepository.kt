package com.elinext.holidays.features.holidaysApi


class HolidaysApiRepository(
    private val remoteDataSource: HolidaysRemoteDataSource
) {

    suspend fun getCountries() = remoteDataSource.getCountries()
}