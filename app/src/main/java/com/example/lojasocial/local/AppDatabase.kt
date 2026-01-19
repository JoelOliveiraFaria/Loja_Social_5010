package com.example.lojasocial.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TrackedPedidoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedPedidoDao(): TrackedPedidoDao
}