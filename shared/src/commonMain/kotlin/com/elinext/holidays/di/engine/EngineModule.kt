package com.elinext.holidays.di.engine

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import com.elinext.holidays.di.EngineSDK

internal val engineModule = DI.Module(
    name = "EngineModule",
    init = {
        bind<PageGenerator>() with singleton { PageGenerator() }
    }
)

val EngineSDK.engine: PageGenerator
    get() = EngineSDK.di.instance()