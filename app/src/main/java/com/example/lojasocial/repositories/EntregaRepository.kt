package com.example.lojasocial.repositories

import com.example.lojasocial.models.Entrega

interface EntregaRepository {
    suspend fun criarEntrega(encomenda: Entrega): String // retorna id criado
    suspend fun atualizarEntrega(encomenda: Entrega)
    suspend fun getEntregaById(encomendaId: String): Entrega?
}

