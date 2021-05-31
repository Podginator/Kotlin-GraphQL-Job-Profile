package org.skyworkz.profile.web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.SecurityUtil.roles
import org.skyworkz.profile.config.Roles
import org.skyworkz.profile.web.controllers.*
import org.skyworkz.profile.web.handlers.LoggingHandler

class Router(private val healthController: HealthController,
             private val authController: AuthController,
             private val graphQLController: GraphQLController,
             private val webNotificationController: WebNotificationController,
             private val loggingHandler : LoggingHandler) {

    fun register(app: Javalin) {
        val anyone = roles(Roles.ANYONE, Roles.AUTHENTICATED)

        app.before(loggingHandler::handle)

        app.routes {

            path ("health") {
                get(healthController::get, anyone)
            }

            path("notify") {
                post(webNotificationController::post, anyone)
             }

            path ("v1/auth") {
                path ("signUp") {
                    post(authController::signUp, anyone)
                }

                path ("signIn") {
                    post(authController::signIn, anyone)
                }
            }

            path ("graphQL") {
                post(graphQLController::post, roles(Roles.AUTHENTICATED, Roles.API_KEY))
            }
        }
    }
}