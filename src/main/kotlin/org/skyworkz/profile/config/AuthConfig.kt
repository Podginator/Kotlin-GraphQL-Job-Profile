package org.skyworkz.profile.config

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.core.JavalinConfig
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.util.JWTProvider

internal enum class Roles : Role {
    ANYONE, AUTHENTICATED, API_KEY
}

class AuthConfig(private val jwtProvider: JWTProvider) {
    fun configure(app: JavalinConfig) {
        app.accessManager { handler, ctx, permittedRoles ->
            val jwtToken = getJwtHeader(ctx)
            val user = jwtToken?.let { getUser(it) }
            val userRole = jwtToken?.let { getRole(it) } ?: getApiKeyOrAnyone(ctx)
            permittedRoles.takeIf { !it.contains(userRole) }?.apply { throw ForbiddenResponse() }


            ctx.attribute("user", user)
            ctx.attribute("role", userRole)
            handler.handle(ctx)
        }
    }

    private fun getJwtHeader(ctx: Context) : DecodedJWT? {
        val tokenHeader = ctx.header("Authorization")?.substringAfter("Bearer")?.trim()
            ?: return null

        return try { jwtProvider.decodeJWT(tokenHeader) } catch (e : Exception) { throw ForbiddenResponse() }
    }

    private fun getUser(token: DecodedJWT) : User? {
        return User(
            email = token.subject,
            id = token.getClaim("id").asInt(),
            firstName = token.getClaim("firstName").asString(),
            lastName = token.getClaim("lastName").asString()
        )
    }

    private fun getRole(token: DecodedJWT?) : Role? {
        val userRole = token?.getClaim("role")?.asString() ?: return null
        return Roles.valueOf(userRole)
    }

    private fun getApiKeyOrAnyone(ctx: Context) : Role {
        val apiKeySend = ctx.header("x-api-key")
        val apiKeyParam = System.getenv("API_KEY")

        return if (apiKeySend == apiKeyParam) Roles.API_KEY else Roles.ANYONE
    }

}