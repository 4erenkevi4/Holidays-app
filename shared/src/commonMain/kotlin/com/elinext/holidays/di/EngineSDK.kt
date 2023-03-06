package com.elinext.holidays.di

import org.kodein.di.*
import com.elinext.holidays.core.coreModule
import com.elinext.holidays.di.engine.engineModule
import com.elinext.holidays.features.featureModule

object EngineSDK {

    internal val di: DirectDI
        get() = requireNotNull(directDI)
    private var directDI: DirectDI? = null


    fun init(configuration: Configuration) {
        val configurationModule = DI.Module(
            name = "configurationModule",
            init = {
                bind<Configuration>() with singleton { configuration }
            }
        )

        if (directDI != null) {
            directDI = null
        }

        val direct = DI {
            importAll(
                configurationModule,
                engineModule,
                featureModule,
                coreModule
            )
        }.direct

        directDI = direct
    }
}