package com.ahmed.model

import com.ahmed.store.DbConfig
import com.ahmed.store.dbConfigStore
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.sql.SQLException
import kotlin.time.Duration.Companion.seconds
import studentmanagement.composeapp.generated.resources.Res

interface DatabaseEventContract{
    val success: Boolean
    val message: String?
}
sealed class DatabaseEvent : DatabaseEventContract{
    data class ConnectionChanged(override val success: Boolean, override val message: String? = null) : DatabaseEvent()
    data class InitializationCompleted(override val success: Boolean, override val message: String) : DatabaseEvent()
    data class ResetCompleted(override val success: Boolean, override val message: String) : DatabaseEvent()
    data class DemoDataLoaded(override val success: Boolean, override val message: String) : DatabaseEvent()
}

private val objectFiles = listOf(
    "files/sql/course_objects.sql",
    "files/sql/enrollment_objects.sql",
    "files/sql/student_objects.sql"
)

private const val SCHEMA_FILE = "files/sql/schema.sql"
private const val DROP_TABLES = "files/sql/drop_tables.sql"
private const val DEMO_DATA = "files/sql/demo_data.sql"

object DatabaseManager {
    private var initialized = false
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()
    private var connectionMonitorJob: Job? = null
    private var dataSource: HikariDataSource? = null
    private var currentConfig: DbConfig? = null

    private val _databaseEvent = MutableStateFlow<DatabaseEvent?>(null)
    val databaseEvent: StateFlow<DatabaseEvent?> = _databaseEvent.asStateFlow()

    val coroutineContext = Dispatchers.IO + SupervisorJob()

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        GlobalScope.launch(coroutineContext) {
            if (initialized && currentConfig == dbConfigStore.get()) return@launch
            val storeConfig = dbConfigStore.get()
            if (storeConfig != null) {
                try {
                    val config = HikariConfig().apply {
                        driverClassName = storeConfig.driverClassName
                        this.jdbcUrl = "jdbc:mysql://${storeConfig.host}:${storeConfig.port}/${storeConfig.databaseName}"
                        this.username = storeConfig.username
                        this.password = storeConfig.password
                        maximumPoolSize = storeConfig.maximumPoolSize
                    }
                    
                    // Close existing connection if configuration has changed
                    if (currentConfig != storeConfig) {
                        dispose()
                        currentConfig = storeConfig
                    }
                    
                    dataSource = HikariDataSource(config)
                    Database.connect(dataSource!!)
                    initialized = true
                    _connectionStatus.value = true
                    startConnectionMonitoring()
                } catch (e: Exception) {
                    println("Failed to connect to database: ${e.message}")
                    _connectionStatus.value = false
                    currentConfig = null
                }
            } else {
                println("Database Config not found, using default values.")
                _connectionStatus.value = false
                currentConfig = null
            }
        }
    }

    suspend fun testConnection(dbConfig: DbConfig): Boolean {
        return withContext(coroutineContext) {
            try {
                val config = HikariConfig().apply {
                    jdbcUrl = "jdbc:mysql://${dbConfig.host}:${dbConfig.port}/${dbConfig.databaseName}"
                    username = dbConfig.username
                    password = dbConfig.password
                    driverClassName = dbConfig.driverClassName
                    maximumPoolSize = 1 // For testing, a single connection is sufficient
                    connectionTimeout = 5000 // 5 seconds
                }
                HikariDataSource(config).use { testDataSource ->
                    testDataSource.connection.use { connection ->
                        connection.isValid(1) // Timeout for the validation query in seconds
                    }
                }

            } catch (e: Exception) {
                println("Test connection failed: ${e.message}")
                false
            }
        }
    }

    private fun startConnectionMonitoring() {
        connectionMonitorJob?.cancel()
        connectionMonitorJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                checkConnection()
                delay(5.seconds)
            }
        }
    }

    private suspend fun checkConnection() {
        try {
            val storeConfig = dbConfigStore.get()
            // Only check the connection if we have a config and it matches the current config
            if (storeConfig != null && storeConfig == currentConfig) {
                dataSource?.connection?.use { connection ->
                    val isValid = connection.isValid(1)
                    if (_connectionStatus.value != isValid) {
                        _connectionStatus.value = isValid
                        _databaseEvent.value = DatabaseEvent.ConnectionChanged(
                            success = isValid,
                            message = if (isValid) "Connection established" else "Connection lost"
                        )
                    }
                }
            } else if (storeConfig != currentConfig) {
                // Configuration has changed, reinitialize the connection
                init()
            }
        } catch (e: SQLException) {
            if (_connectionStatus.value) {  // Only emit event if we were previously connected
                _connectionStatus.value = false
                println("Database connection check failed: ${e.message}")
                _databaseEvent.value = DatabaseEvent.ConnectionChanged(
                    success = false,
                    message = "Connection check failed: ${e.message}"
                )
            }
        }
    }

    fun isDatabaseReady(): Boolean {
        return initialized && _connectionStatus.value
    }

    fun dispose() {
        connectionMonitorJob?.cancel()
        dataSource?.close()
    }

    suspend fun initializeDatabase(): Pair<Boolean, String> {
        if (!initialized) {
            println("Cannot initialize database: No active database connection.")
            _databaseEvent.value = DatabaseEvent.InitializationCompleted(false, "Database not connected. Cannot initialize.")
            return Pair(false, "Database not connected. Cannot initialize.")
        }

        return try {
            println("\nStarting database schema initialization...")

            // Initialize schema first
            println("Executing schema file: $SCHEMA_FILE")
            val schemaSuccess = initializeSchema()
            if (!schemaSuccess) {
                _databaseEvent.value = DatabaseEvent.InitializationCompleted(false, "Failed to initialize database schema")
                return Pair(false, "Failed to initialize database schema")
            }

            // Initialize all SQL objects
            println("\nInitializing database objects...")
            objectFiles.forEach { filePath ->
                println("Processing SQL objects file: $filePath")
                try {
                    initializeDatabaseObject(filePath)
                } catch (e: Exception) {
                    val message = "Failed to process $filePath: ${e.message}"
                    println(message)
                    throw e
                }
            }

            println("Database schema and all SQL objects initialization completed successfully!")
            _databaseEvent.value = DatabaseEvent.InitializationCompleted(true, "Database initialized successfully")
            Pair(true, "Database initialized successfully")
        } catch (e: Exception) {
            val message = "Failed to initialize database: ${e.message}\n${e.stackTraceToString()}"
            println(message)
            _databaseEvent.value = DatabaseEvent.InitializationCompleted(false, message)
            Pair(false, message)
        }
    }

    suspend fun resetDatabase(): Pair<Boolean, String> {
        if (!initialized) {
            println("Cannot reset database: No active database connection.")
            _databaseEvent.value = DatabaseEvent.ResetCompleted(false, "Database not connected. Cannot reset.")
            return Pair(false, "Database not connected. Cannot reset.")
        }

        return try {
            println("\nStarting database reset...")

            // Execute drop tables SQL
            val dropTablesBytes: ByteArray = Res.readBytes(DROP_TABLES)
            val dropStatements = String(dropTablesBytes)
                .split(";")
                .filter { it.trim().isNotEmpty() }

            transaction {
                dropStatements.forEach { statement ->
                    exec(statement.trim())
                }
            }

            // Reinitialize the database
            val (success, message) = initializeDatabase()
            if (success) {
                _databaseEvent.value = DatabaseEvent.ResetCompleted(true, "Database reset successfully")
                Pair(true, "Database reset successfully")
            } else {
                throw Exception("Failed to reinitialize database after reset: $message")
            }
        } catch (e: Exception) {
            val message = "Failed to reset database: ${e.message}"
            println("$message\n${e.stackTraceToString()}")
            _databaseEvent.value = DatabaseEvent.ResetCompleted(false, message)
            Pair(false, message)
        }
    }

    private suspend fun initializeSchema(): Boolean {
        return try {
            val bytes: ByteArray = Res.readBytes(SCHEMA_FILE)
            val schema = String(bytes)
            val statements = schema.split(";").filter { it.trim().isNotEmpty() }

            transaction {
                statements.forEach { statement ->
                    exec(statement.trim())
                }
            }
            true
        } catch (e: Exception) {
            println("Schema initialization failed: ${e.message}")
            false
        }
    }

    private suspend fun initializeDatabaseObject(filePath: String) {
        val bytes: ByteArray = Res.readBytes(filePath)
        val content = String(bytes).lines()
            .filter { line -> line.isNotEmpty() && !line.trim().startsWith("--") }
            .joinToString("\n")

        transaction {
            // Process views and regular statements first
            val viewStatements = content
                .substringBefore("DELIMITER")
                .split(";")
                .filter { it.trim().isNotEmpty() }

            viewStatements.forEach { statement ->
                println("Executing statement: $statement")
                exec(statement)
            }

            // Process stored procedures and functions
            if (content.contains("DELIMITER")) {
                val procedureContent = content.substringAfter("DELIMITER //")
                val procedures = procedureContent
                    .split("//")
                    .filter { it.trim().isNotEmpty() }
                    .map { it.trim() }
                    .filter { it.startsWith("CREATE") }

                procedures.forEach { procedure ->
                    println("Executing procedure: $procedure")
                    exec(procedure)
                }
            }
        }
    }

    suspend fun loadDemoData(): Pair<Boolean, String> {
        if (!initialized) {
            println("Cannot load demo data: No active database connection.")
            _databaseEvent.value = DatabaseEvent.DemoDataLoaded(false, "Database not connected. Cannot load demo data.")
            return Pair(false, "Database not connected. Cannot load demo data.")
        }

        return try {
            println("\nLoading demo data...")

            val demoDataBytes: ByteArray = Res.readBytes(DEMO_DATA)
            val demoStatements = String(demoDataBytes)
                .split(";")
                .filter { it.trim().isNotEmpty() }

            transaction {
                demoStatements.forEach { statement ->
                    exec(statement.trim())
                }
            }

            println("Demo data loaded successfully!")
            _databaseEvent.value = DatabaseEvent.DemoDataLoaded(true, "Demo data loaded successfully")
            Pair(true, "Demo data loaded successfully")
        } catch (e: Exception) {
            val message = "Failed to load demo data: ${e.message}"
            println("$message\n${e.stackTraceToString()}")
            _databaseEvent.value = DatabaseEvent.DemoDataLoaded(false, message)
            Pair(false, message)
        }
    }
}
