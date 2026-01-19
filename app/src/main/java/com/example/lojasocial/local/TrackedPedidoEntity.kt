package com.example.lojasocial.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_pedidos")
data class TrackedPedidoEntity(
    @PrimaryKey val pedidoId: String,
    val addedAt: Long = System.currentTimeMillis(),
    val note: String? = null
)
