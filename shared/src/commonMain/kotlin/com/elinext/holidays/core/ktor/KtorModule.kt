package com.elinext.holidays.core.ktor

import io.ktor.client.*
import io.ktor.http.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import com.elinext.holidays.core.ktor.engine.HttpEngineFactory
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.kotlinx.serializer.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

internal val ktorModule = DI.Module(
    name = "KtorModule",
    init = {

        bind<HttpEngineFactory>() with singleton { HttpEngineFactory() }
        bind<HttpClient>() with singleton {

            val engine = instance<HttpEngineFactory>().createEngine()

            HttpClient(engine) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.ALL
                }
                install(ContentNegotiation) {
                    json()
                }

                defaultRequest {
                    host = "holidays.elinext.com/api"
                    url {
                        protocol = URLProtocol.HTTP
                    }
                }
            }
        }
    }
)