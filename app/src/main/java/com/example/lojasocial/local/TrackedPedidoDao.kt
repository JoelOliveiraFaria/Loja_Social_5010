package com.example.lojasocial.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedPedidoDao {

    @Query("SELECT pedidoId FROM tracked_pedidos")
    fun observeTrackedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TrackedPedidoEntity)

    @Query("DELETE FROM tracked_pedidos WHERE pedidoId = :id")
    suspend fun delete(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM tracked_pedidos WHERE pedidoId = :id)")
    suspend fun isTracked(id: String): Boolean
}