package org.skyworkz.profile.web.controllers

import io.javalin.http.Context
import org.skyworkz.profile.config.Roles
import org.skyworkz.profile.domain.model.request.*
import org.skyworkz.profile.domain.service.UserService
import org.skyworkz.profile.util.JWTProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthController(private val userService : UserService,
                     private val jwtProvider: JWTProvider) {

    private val logger : Logger = LoggerFactory.getLogger(AuthController::class.java)

    fun signIn(ctx: Context) {
        ctx.bodyAsClass(UserLoginDto::class.java).let { login ->
            userService.authenticate(login.email, login.password)?.let { user ->
                jwtProvider.createJWT(user, Roles.AUTHENTICATED)?.let {
                    ctx.json(hashMapOf("token" to it))
                }
            }
        }
    }

    fun signUp (ctx: Context) {
        ctx.bodyAsClass(SignUpDto::class.java).let {
            userService.create(it.toUser())
            logger.debug("Creating User $it")
            ctx.json(hashMapOf("verified" to false))
        }
    }

}