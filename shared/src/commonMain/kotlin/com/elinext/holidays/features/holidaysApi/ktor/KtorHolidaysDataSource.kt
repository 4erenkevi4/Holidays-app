package com.elinext.holidays.features.holidaysApi.ktor

import com.elinext.holidays.features.holidaysApi.HolidaysRemoteDataSource
import com.elinext.holidays.models.CountryModel
import com.elinext.holidays.models.DayModel
import com.elinext.holidays.models.Holiday
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class KtorHolidaysDataSource(
    private val httpClient: HttpClient,
    val json: Json
): HolidaysRemoteDataSource {

    override suspend fun getCountries(): List<CountryModel> {
        val httpRequest = httpClient.get {
            url {
                path("countries/all")
            }
        }

        return json.decodeFromString(
            ListSerializer(CountryModel.serializer()),
            httpRequest.bodyAsText()
        )
    }

    override suspend fun getAllDays(): Map<Int, List<Holiday>> {
        val httpRequest = httpClient.get {
            url {
                path("days/all")
            }
        }
        return json.decodeFromString(
            MapSerializer(Int.serializer(),ListSerializer(Holiday.serializer())),
            httpRequest.bodyAsText()
        )
    }

    override suspend fun searchDay(date: String, id: String): DayModel {
        val httpRequest = httpClient.get {
            url {
                path("day/${date}/${id}")
            }
        }
        return json.decodeFromString(
            DayModel.serializer(),
            httpRequest.bodyAsText()
        )
    }

    override suspend fun getQuantityWorkingDays(year: String, id: String): Int {
        val httpRequest = httpClient.get {
            url {
                path("year/workingDays/${year}/${id}")
            }
        }
        return json.decodeFromString(
            Int.serializer(),
            httpRequest.bodyAsText()
        )
    }
}
