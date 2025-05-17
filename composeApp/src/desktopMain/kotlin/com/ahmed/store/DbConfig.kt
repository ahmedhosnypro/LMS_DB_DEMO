package com.ahmed.store

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable

@Serializable
data class DbConfig(
    val databaseName: String = "student_management",
    val host: String = "localhost",
    val port: Int = 3308,
    val username: String = "root",
    val password: String = "root",
    val driverClassName: String = "com.mysql.cj.jdbc.Driver",
    val maximumPoolSize: Int = 10,
)

const val settingsFile = "dbConfigFile.json"

val dbConfigPath = Path(appStorage.toString(), settingsFile)

val dbConfigStore: KStore<DbConfig> = storeOf(file =dbConfigPath)
