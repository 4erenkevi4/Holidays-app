package com.elinext.holidays.features.holidaysApi.ktor

import com.elinext.holidays.features.holidaysApi.HolidaysRemoteDataSource
import com.elinext.holidays.models.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class KtorHolidaysDataSource(
    private val httpClient: HttpClient, val json: Json
) : HolidaysRemoteDataSource {

    override suspend fun getCountries(): Pair<HttpStatusCode, List<CountryModel>?> {
        return try {
            val httpRequest = httpClient.get {
                url {
                    path("countries/all")
                }
            }
            val result = if (httpRequest.status.isSuccess()) json.decodeFromString(
                ListSerializer(CountryModel.serializer()), httpRequest.bodyAsText()
            )
            else null
            Pair(httpRequest.call.response.status, result)
        } catch (e: Exception) {
            getErrorResponse(e)
        }
    }

    override suspend fun getAllDays(): Pair<HttpStatusCode, Holidays?> {
        return try {
            val httpRequest = httpClient.get {
                url {
                    path("days/all")
                }
            }
            val result = if (httpRequest.status.isSuccess()) json.decodeFromString(
                Holidays.serializer(),
                //  MapSerializer(Int.serializer(),ListSerializer(Holiday.serializer())),
                httpRequest.bodyAsText()
            )
            else null
            return Pair(httpRequest.status, result)

        } catch (e: Exception) {
            getErrorResponse(e)
        }
    }

    override suspend fun searchDay(date: String, id: String): Pair<HttpStatusCode, DayModel?> {
        return try {

            val httpRequest = httpClient.get {
                url {
                    path("day/${date}/${id}")
                }
            }
            val result = if (httpRequest.status.isSuccess()) json.decodeFromString(
                DayModel.serializer(), httpRequest.bodyAsText()
            )
            else null
            Pair(httpRequest.status, result)
        } catch (e: java.lang.Exception) {
            getErrorResponse(e)
        }
    }

    override suspend fun getQuantityWorkingDays(
        year: String,
        id: String
    ): Pair<HttpStatusCode, Number?> {
        return try {


            val httpRequest = httpClient.get {
                url {
                    path("year/workingDays/${year}/${id}")
                }
            }
            val result = if (httpRequest.status.isSuccess()) json.decodeFromString(
                Int.serializer(), httpRequest.bodyAsText()
            )
            else null
            Pair(httpRequest.status, result)
        }
        catch (e: Exception){
            getErrorResponse(e)
        }
    }

    private fun getErrorResponse(e: Exception) =
        Pair(HttpStatusCode(444, e.message ?: "internal request error"), null)
}
