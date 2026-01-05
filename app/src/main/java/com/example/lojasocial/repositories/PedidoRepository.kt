package com.example.lojasocial.repositories

import com.example.lojasocial.models.Pedido
import com.example.lojasocial.models.PedidoStatus
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {

    fun getPedidosPorStatus(status: PedidoStatus): Flow<List<Pedido>>

    suspend fun getPedidoById(pedidoId: String): Pedido?

    suspend fun aceitarPedido(pedidoId: String)

    suspend fun recusarPedido(
        pedidoId: String,
        motivo: String
    )
}
