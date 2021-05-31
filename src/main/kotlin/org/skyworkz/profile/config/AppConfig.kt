package org.skyworkz.profile.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.plugin.json.JavalinJackson
import org.skyworkz.profile.web.Router
import org.skyworkz.profile.web.handlers.ErrorHandler
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.skyworkz.profile.messages.CoroutineEventBroker
import java.lang.System.getProperty
import java.text.SimpleDateFormat

class AppConfig : KoinComponent {

    private val router : Router by inject()
    private val authConfig : AuthConfig by inject()
    private val eventBroker : CoroutineEventBroker by inject()

    fun setup(): Javalin {
        SQLConfig.setUpDatabase()
        eventBroker.consume()
        return Javalin.create {
            it.enableCorsForAllOrigins()
            authConfig.configure(it)
        }
            .also { app ->
                this.configureMapper()
                val port : Int = getProperty("server_port")?.toIntOrNull() ?: 7000
                router.register(app)
                ErrorHandler.register(app)
                app.start(port)
            }
    }

    private fun configureMapper() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        JavalinJackson.configure(jacksonObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(dateFormat)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        )
    }
}