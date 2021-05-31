package org.skyworkz.profile.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.core.security.Role
import org.skyworkz.profile.domain.model.User
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.util.*

class JWTProvider(val algorithm : Algorithm) {

    fun decodeJWT(token: String): DecodedJWT = JWT.require(Cipher.algorithm).build().verify(token)

    fun createJWT(user: User, role: Role): String? =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject(user.email)
            .withClaim("id", user.id!!)
            .withClaim("firstName", user.firstName)
            .withClaim("lastName", user.lastName)
            .withClaim("role", role.toString())
            .withExpiresAt(
                Date.from(
                    LocalDate.now().plus(Period.ofYears(1)).atStartOfDay().toInstant(ZoneOffset.UTC)
                )
            )
            .sign(algorithm)
}