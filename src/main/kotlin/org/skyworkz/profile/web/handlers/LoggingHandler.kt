package org.skyworkz.profile.web.handlers

import io.javalin.http.Context
import io.javalin.http.Handler
import org.slf4j.MDC
import java.util.*


class LoggingHandler : Handler {

    private val CORRELATION_ID_HEADER_NAME = "X-Correlation-Id"
    private val CORRELATION_ID_LOG_VAR_NAME = "correlationId"

    /**
     * Provide a logger with the correlation id set.
     */
    override fun handle(ctx: Context) {
        val correlationId = ctx.header(CORRELATION_ID_HEADER_NAME) ?: UUID.randomUUID().toString()
        
        MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId)
    }

}