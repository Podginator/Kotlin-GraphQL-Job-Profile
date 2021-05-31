package org.skyworkz.profile

import org.skyworkz.profile.config.AppConfig
import org.skyworkz.profile.config.KoinConfig
import org.koin.core.context.startKoin
import org.skyworkz.profile.config.SSMConfig

fun main() {
    startKoin {
        printLogger()
        modules(KoinConfig.allModules)
    }
    AppConfig().setup()
}