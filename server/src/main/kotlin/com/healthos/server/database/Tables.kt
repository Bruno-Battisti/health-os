package com.healthos.server.database

import org.jetbrains.exposed.v1.core.Table

object Accounts : Table("accounts") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

object UserProfiles : Table("user_profiles") {
    val accountId = varchar("account_id", 36).references(Accounts.id)
    val name = varchar("name", 255)
    val birthDateEpochDay = long("birth_date_epoch_day")
    val heightCm = float("height_cm")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(accountId)
}

object WeightEntries : Table("weight_entries") {
    val remoteId = varchar("remote_id", 36)
    val accountId = varchar("account_id", 36).references(Accounts.id)
    val weight = float("weight")
    val dateEpochDay = long("date_epoch_day")
    val note = varchar("note", 500).nullable()
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(remoteId)
}
