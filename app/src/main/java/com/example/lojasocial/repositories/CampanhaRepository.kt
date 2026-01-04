package com.example.lojasocial.repositories

import com.example.lojasocial.models.Campanha
import kotlinx.coroutines.flow.Flow

interface CampanhasRepository {
    fun observeCampanhas(): Flow<ResultWrapper<List<Campanha>>>
    suspend fun criarCampanha(c: Campanha): ResultWrapper<Unit>
    suspend fun atualizarCampanha(c: Campanha): ResultWrapper<Unit>
    suspend fun eliminarCampanha(id: String): ResultWrapper<Unit>
    suspend fun obterCampanha(id: String): ResultWrapper<Campanha>
}
