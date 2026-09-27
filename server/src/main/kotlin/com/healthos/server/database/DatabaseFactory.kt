package com.healthos.server.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.sql.DataSource

object DatabaseFactory {

    fun init(): Database {
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/healthos"
        val user = System.getenv("DB_USER") ?: "healthos"
        val password = System.getenv("DB_PASSWORD") ?: "healthos"

        return connect(createHikariDataSource(url, user, password))
    }

    fun connect(dataSource: DataSource): Database {
        val database = Database.connect(dataSource)
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Accounts, UserProfiles, WeightEntries)
        }
        return database
    }

    private fun createHikariDataSource(url: String, user: String, password: String): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = "org.postgresql.Driver"
            username = user
            this.password = password
            maximumPoolSize = 10
        }
        return HikariDataSource(config)
    }
}
