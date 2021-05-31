package org.skyworkz.profile.web.handlers

import com.auth0.jwt.exceptions.TokenExpiredException
import io.javalin.Javalin
import org.eclipse.jetty.http.HttpStatus
import org.skyworkz.profile.domain.exception.*
import org.slf4j.LoggerFactory


object ErrorHandler {

    private val LOG = LoggerFactory.getLogger(ErrorHandler::class.java)
    fun register(app: Javalin) {
        app.exception(Exception::class.java) { e, ctx ->
            LOG.error("Exception occurred for req -> ${ctx.url()}", e)
            val error = mapOf("Errors" to listOf("Unknown Error Occured"))
            ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }

        app.exception(InvalidLoginException::class.java) { e, ctx ->
            LOG.error("Invalid Login -> ${ctx.url()}", e)
            val error = mapOf("Errors" to listOf("Invalid email or password"))
            ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
        }

        app.exception(TokenExpiredException::class.java) { e, ctx ->
            LOG.error("Token has expired", e)
            val error = mapOf("Errors" to listOf("Token Expired"))
            ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
        }


        app.exception(UserNotFoundException::class.java) { e, ctx ->
            LOG.error("Exception occurred for req -> ${ctx.url()}", e)
            val error = mapOf("Errors" to listOf("User Not Found"))
            ctx.json(error).status(HttpStatus.NOT_FOUND_404)
        }

        app.exception(InvalidAccessException::class.java) { e, ctx ->
            LOG.error("Exception occurred for req -> ${ctx.url()}", e)
            val error = mapOf("Errors" to listOf("Forbidden"))
            ctx.json(error).status(HttpStatus.FORBIDDEN_403)
        }

        app.exception(DuplicateUserException::class.java) { e, ctx ->
            LOG.error("Exception occurred for req -> ${ctx.url()}", e)
            val error = mapOf("Errors" to listOf("User Already Exists"))
            ctx.json(error).status(HttpStatus.CONFLICT_409)
        }
    }
}