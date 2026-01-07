package com.example.lojasocial.repositories

import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import kotlinx.coroutines.flow.Flow
interface EntregaRepository {
    suspend fun criarEntrega(entrega: Entrega): String // retorna id criado
    suspend fun atualizarEntrega(entrega: Entrega)
    suspend fun getEntregaById(entregaId: String): Entrega?

    fun getEntregasPorStatus(status: EntregaStatus): Flow<List<Entrega>>
}

