package org.skyworkz.profile.config


/**
 * Values retrieved from SSM and added to Docker Environments
 */
object SSMConfig {
    val jwtSecret = System.getenv("JWT_KEY")
    val sqlPassword = System.getenv("SQL_PASSWORD")
    val apiKey = System.getenv("API_KEY")
    val sqlUrl = System.getenv("SQL_URL")
}