package com.example.lojasocial.repositories

import com.example.lojasocial.local.TrackedPedidoDao
import com.example.lojasocial.local.TrackedPedidoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackedPedidoRepository @Inject constructor(
    private val dao: TrackedPedidoDao
) {
    fun observeTrackedIds(): Flow<Set<String>> =
        dao.observeTrackedIds().map { it.toSet() }

    suspend fun toggle(pedidoId: String) {
        if (dao.isTracked(pedidoId)) dao.delete(pedidoId)
        else dao.insert(TrackedPedidoEntity(pedidoId = pedidoId))
    }
}