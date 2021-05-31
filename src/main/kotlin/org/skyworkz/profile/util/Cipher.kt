package org.skyworkz.profile.util

import com.auth0.jwt.algorithms.Algorithm
import org.skyworkz.profile.config.SSMConfig

object Cipher {
    val algorithm = Algorithm.HMAC256(SSMConfig.jwtSecret)
}