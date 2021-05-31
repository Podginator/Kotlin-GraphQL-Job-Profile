package org.skyworkz.profile.web.controllers

import io.javalin.http.Context

class HealthController {
    fun get (ctx: Context) {
        ctx.status(200)
    }
}