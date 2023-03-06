package com.elinext.holidays.core

import org.kodein.di.DI
import com.elinext.holidays.core.ktor.ktorModule
import com.elinext.holidays.core.serialization.serializationModule

val coreModule = DI.Module {
    importAll(
        ktorModule,
        serializationModule
    )
}