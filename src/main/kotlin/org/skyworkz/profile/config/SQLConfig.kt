package org.skyworkz.profile.config

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.skyworkz.profile.domain.model.sql.*

object SQLConfig {
    fun setUpDatabase() {
        Database.connect(
            "jdbc:mysql://${System.getenv("SQL_URL")}:3306/skyworkz?serverTimezone=UTC", driver = "com.mysql.cj.jdbc.Driver", user = System.getenv("SQL_USER"), password = SSMConfig.sqlPassword
        )

        transaction {
            SchemaUtils.create(Jobs, Tags, Profiles, JobTags, ProfileTag, ProfileSubscriptions, CV)
            commit()
        }
    }
}