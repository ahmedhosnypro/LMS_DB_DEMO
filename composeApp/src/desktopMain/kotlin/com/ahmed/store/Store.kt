package com.ahmed.store

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

val appStorage = Path(System.getProperty("user.home"), ".student_management")

@OptIn(DelicateCoroutinesApi::class)
fun initAppStorage() {
    with(SystemFileSystem) {
        println("System File System: $this")
        println("User Home: ${System.getProperty("user.home")}")
        println("App Storage: $appStorage")
        if (!exists(appStorage)
        ) createDirectories(appStorage)

        GlobalScope.launch {
            dbConfigStore.set(DbConfig())
        }
    }
}