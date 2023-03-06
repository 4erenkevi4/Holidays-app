package com.elinext.holidays.features.holidaysApi.ktor

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import com.elinext.holidays.features.holidaysApi.HolidaysRemoteDataSource
import com.elinext.holidays.models.CountryModel
import io.ktor.http.*

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
}

/*@GET("days/all")
suspend fun getAllDays(
):  Response<Holidays>

@GET("day/{dateDay}/{officeID}")
suspend fun searchDay(
    @Path("dateDay") date: String,
    @Path("officeID") id: String
): Response<DayModel>

@GET("year/workingDays/{year}/{officeID}")
suspend fun getQuantityWorkingDays(
    @Path("year") year: String,
    @Path("officeID") id: String
): Response<Number>

@GET("countries/all")
suspend fun getListCountry(
): Response<List<CountryModel>>*/
