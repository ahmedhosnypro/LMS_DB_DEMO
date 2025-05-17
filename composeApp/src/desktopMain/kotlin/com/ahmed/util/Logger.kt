package com.ahmed.util

//import com.ahmed.store.appStorage
import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.io.files.Path
//import kotlinx.io.files.SystemFileSystem
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
//import kotlinx.io.Buffer

enum class LogLevel(val label: String, val color: String) {
    DEBUG("DEBUG", "\u001B[36m"),    // Cyan
    INFO("INFO", "\u001B[32m"),      // Green
    WARNING("WARN", "\u001B[33m"),   // Yellow
    ERROR("ERROR", "\u001B[31m"),    // Red
    CRITICAL("CRIT", "\u001B[35;1m") // Bright Magenta
}

@OptIn(DelicateCoroutinesApi::class)
object Logger {
    private const val RESET = "\u001B[0m"
//    private val logsDir = Path(appStorage.toString(), "logs")
//    private var currentLogFile: Path? = null

//    init {
//        with(SystemFileSystem) {
//            if (!exists(logsDir)) {
//                createDirectories(logsDir)
//            }
//            updateLogFile()
//        }
//    }

    private fun getCurrentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${
            dateTime.dayOfMonth.toString().padStart(2, '0')
        } " +
                "${dateTime.hour.toString().padStart(2, '0')}:${
                    dateTime.minute.toString().padStart(2, '0')
                }:${dateTime.second.toString().padStart(2, '0')}"
    }

    private fun updateLogFile() {
        val now = getCurrentDateTime()
        val fileName =
            "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
//        currentLogFile = Path(logsDir.toString(), "log_$fileName.txt")
    }

    private fun formatMessage(level: LogLevel, message: String): String {
        val timestamp = formatDateTime(getCurrentDateTime())
        return "[$timestamp] ${level.label}: $message"
    }

    private fun formatColoredMessage(level: LogLevel, message: String): String {
        return "${level.color}${formatMessage(level, message)}$RESET"
    }

//    private fun writeToFile(message: String) {
//        GlobalScope.launch {
//            with(SystemFileSystem) {
//                updateLogFile()
//                currentLogFile?.let { logFile ->
//                    val rawSink =  sink(logFile, true)
//                    with(rawSink) {
//                        writeToFile(message)
//                    }
//                }
//            }
//        }
//    }

    fun debug(message: String) = log(LogLevel.DEBUG, message)
    fun info(message: String) = log(LogLevel.INFO, message)
    fun warning(message: String) = log(LogLevel.WARNING, message)
    fun error(message: String) = log(LogLevel.ERROR, message)
    fun critical(message: String) = log(LogLevel.CRITICAL, message)

    private fun log(level: LogLevel, message: String) {
        val plainMessage = formatMessage(level, message)
        val coloredMessage = formatColoredMessage(level, message)

        // Print to console with color
        println(coloredMessage)

        // Write to file without color codes
//        writeToFile(plainMessage)
    }
}
