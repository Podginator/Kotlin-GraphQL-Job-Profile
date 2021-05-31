package org.skyworkz.profile.web.graphQL.context

import io.javalin.core.security.Role
import org.skyworkz.profile.domain.model.User

data class AuthContext(
    val user : User?,
    val role : Role
)