package com.elinext.holidays.features

import org.kodein.di.DI
import com.elinext.holidays.features.holidaysApi.holidaysApiModule

val featureModule = DI.Module {
    importAll(
        holidaysApiModule
    )
}