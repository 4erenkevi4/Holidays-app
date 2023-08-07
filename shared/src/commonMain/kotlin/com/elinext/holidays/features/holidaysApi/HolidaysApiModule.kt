package com.elinext.holidays.features.holidaysApi

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.features.holidaysApi.ktor.KtorHolidaysDataSource

internal val holidaysApiModule = DI.Module(
    name = "HolidaysApiModule",
    init = {
        bind<HolidaysRemoteDataSource>() with singleton {
            KtorHolidaysDataSource(
                httpClient = instance(),
                json = instance()
            )
        }

        bind<HolidaysApiRepository>() with singleton {
            HolidaysApiRepository(
                remoteDataSource = instance()
            )
        }
    }
)

object HolidaysApiModule {

    val holidaysRepository: HolidaysApiRepository
        get() = EngineSDK.di.instance()
}

val EngineSDK.apiModule: HolidaysApiModule
    get() = HolidaysApiModule
 