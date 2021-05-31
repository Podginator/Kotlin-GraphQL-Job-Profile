package org.skyworkz.profile.domain.model.request

import org.skyworkz.profile.domain.model.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

data class SignUpDto (
        val firstName: String,
        val lastName: String,
        val email : String,
        val password : String,
        val minimumHourlyRate: String,
        val available: Boolean
)

fun SignUpDto.toUser() =
    User(
        firstName = this.firstName,
        email = this.email,
        lastName = this.lastName,
        password = BCrypt.hashpw(this.password, BCrypt.gensalt(7)),
        available = this.available,
        minimumHourlyRate = this.minimumHourlyRate.toFloat()
    )
